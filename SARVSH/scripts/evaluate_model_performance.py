
import pandas as pd
import numpy as np
import joblib
import os
from sklearn.model_selection import StratifiedKFold, cross_val_predict
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score

# Define paths
dataset_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'odour_prediction_dataset.csv')
model_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'odour_model.pkl')

def add_derived_features(df):
    """
    Adds engineered features to consistent with training.
    """
    # 1. Heat Interaction (Temp * Humidity)
    df['Heat_Interaction'] = (df['Temperature_C'] * df['Humidity_Pct']) / 100.0

    # 2. VOC Impact (VOC * Temp)
    df['VOC_Temp_Interaction'] = (df['VOC_Index'] * df['Temperature_C']) / 100.0
    
    return df

def evaluate_performance():
    print("--- AeroBins Odour Model Evaluation (Tuned Model) ---\n")
    
    # 1. Load Data
    try:
        df = pd.read_csv(dataset_path)
    except FileNotFoundError:
        print(f"Error: Dataset not found at {dataset_path}")
        return

    # 2. Preprocessing & Feature Eng
    df['Timestamp'] = pd.to_datetime(df['Timestamp'])
    df['Hour'] = df['Timestamp'].dt.hour
    
    # Feature Engineering
    df = add_derived_features(df)
    
    # Matching feature set
    feature_names = [
        'Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour',
        'Heat_Interaction', 'VOC_Temp_Interaction'
    ]
    X = df[feature_names]
    y = df['Odour_Severity']

    # 3. Load Model
    print(f"Loading model from {model_path}...")
    try:
        model = joblib.load(model_path)
    except Exception as e:
        print(f"Error loading model: {e}")
        return

    # 4. Synthesize 'Unseen' Test Cases (Same as before)
    # Note: We must also add derived features to these manually or via function
    print("\n--- Testing on Synthetic Unseen Scenarios ---")
    scenarios = [
        # Case 1: Ideal conditions
        {'Temperature_C': 20.0, 'Humidity_Pct': 40.0, 'Gas_Resistance_Ohms': 50000, 'VOC_Index': 10, 'Fill_Level_Pct': 5, 'Hour': 8, 'Expected': 'Good'},
        # Case 2: High Fill, but fresh (Likely failure point before)
        {'Temperature_C': 25.0, 'Humidity_Pct': 55.0, 'Gas_Resistance_Ohms': 40000, 'VOC_Index': 50, 'Fill_Level_Pct': 90, 'Hour': 14, 'Expected': 'Moderate'},
        # Case 3: High Temperature, High Fill
        {'Temperature_C': 35.0, 'Humidity_Pct': 80.0, 'Gas_Resistance_Ohms': 10000, 'VOC_Index': 300, 'Fill_Level_Pct': 95, 'Hour': 16, 'Expected': 'Severe'},
        # Case 4: Night time, high humidity
        {'Temperature_C': 22.0, 'Humidity_Pct': 90.0, 'Gas_Resistance_Ohms': 20000, 'VOC_Index': 100, 'Fill_Level_Pct': 50, 'Hour': 23, 'Expected': 'Moderate'}
    ]
    
    scenario_df = pd.DataFrame(scenarios)
    
    # Add derived features to scenarios
    scenario_df = add_derived_features(scenario_df)
    
    X_unseen = scenario_df[feature_names]
    
    predictions = model.predict(X_unseen)
    
    for i, row in scenario_df.iterrows():
        pred = predictions[i]
        # Compare prediction
        match = "[OK]" if pred == row['Expected'] else "[FAIL]"
        print(f"Scenario {i+1}: {row['Expected']} (Expected) vs {pred} (Predicted) {match}")
        
        # If specific case 2 (Fresh but full), check if it improved
        if i == 1: 
             if pred == 'Moderate':
                 print("   -> IMPROVEMENT: Model correctly identified 'Fresh but Full' bin!")
             else:
                 print(f"   -> Inputs: Temp={row['Temperature_C']}, Fill={row['Fill_Level_Pct']}, VOC={row['VOC_Index']}")

if __name__ == "__main__":
    evaluate_performance()
