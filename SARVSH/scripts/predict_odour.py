
import sys
import pandas as pd
import joblib
import os
import warnings

# Suppress warnings
warnings.filterwarnings("ignore")

# Define paths
model_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'odour_model.pkl')

def load_model():
    try:
        return joblib.load(model_path)
    except FileNotFoundError:
        print(f"Error: Model not found at {model_path}")
        sys.exit(1)

def add_derived_features(df):
    """
    Adds engineered features to consistent with training.
    """
    # 1. Heat Interaction (Temp * Humidity)
    df['Heat_Interaction'] = (df['Temperature_C'] * df['Humidity_Pct']) / 100.0

    # 2. VOC Impact (VOC * Temp)
    df['VOC_Temp_Interaction'] = (df['VOC_Index'] * df['Temperature_C']) / 100.0
    
    return df

def predict(args):
    # Expected args: Temperature, Humidity, Gas_Resistance, VOC_Index, Fill_Level, Hour
    if len(args) != 6:
        print("Error: Expected 6 arguments (Temp, Humidity, Gas, VOC, Fill, Hour)")
        sys.exit(1)

    try:
        features = [float(arg) for arg in args]
    except ValueError:
        print("Error: All arguments must be numeric")
        sys.exit(1)

    # Feature names must match training input (before engineering)
    # Note: We create a DF with the raw inputs first
    raw_feature_names = ['Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour']
    df = pd.DataFrame([features], columns=raw_feature_names)

    # Apply same feature engineering as training
    df = add_derived_features(df)
    
    # Ensure column order matches model expectation (though RF is usually name-agnostic in sklearn, 
    # newer versions or wrappers might care, but sklearn array input relies on order if names aren't used. 
    # But since we pass DF to predict, it matches on names if available, or order. 
    # Safe bet is to ensure order matches exactly what was used to fit.)
    
    # The training script used:
    # feature_names = [
    #    'Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour',
    #    'Heat_Interaction', 'VOC_Temp_Interaction'
    # ]
    
    # Reorder columns explicitly to match training
    final_features = [
        'Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour',
        'Heat_Interaction', 'VOC_Temp_Interaction'
    ]
    df = df[final_features]

    model = load_model()
    prediction = model.predict(df)
    
    # Print only the prediction result to stdout
    print(prediction[0])

if __name__ == "__main__":
    # stored in sys.argv[1:]
    predict(sys.argv[1:])
