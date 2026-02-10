
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler, MinMaxScaler
import os

# Define paths
dataset_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'odour_prediction_dataset.csv')
processed_train_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'train_processed.csv')
processed_test_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'test_processed.csv')

def preprocess_data():
    print("--- Starting Preprocessing ---")
    
    # 1. Load Data
    try:
        df = pd.read_csv(dataset_path)
        print(f"Data loaded. Shape: {df.shape}")
    except FileNotFoundError:
        print(f"Error: Dataset not found at {dataset_path}")
        return

    # 2. Handling Missing Values
    # In IoT, sensors often fail or send nulls. We fill them to avoid model crashes.
    # Forward fill is often good for time-series, but here we'll use mean for simplicity in this demo.
    print("Handling missing values...")
    if df.isnull().sum().sum() > 0:
        print(f"Found {df.isnull().sum().sum()} missing values. Imputing...")
        df.fillna(method='ffill', inplace=True) # Forward fill for time-series continuity
    else:
        print("No missing values found.")

    # 3. Feature Engineering & Noise Removal
    # Convert timestamp to Hour (cyclical feature)
    df['Timestamp'] = pd.to_datetime(df['Timestamp'])
    df['Hour'] = df['Timestamp'].dt.hour
    
    # Drop Timestamp as it's not a direct numeric feature for the model
    # We keep the dataframe clean
    df_clean = df.drop(columns=['Timestamp'])

    # Simple noise checks (e.g. Temperature shouldn't be > 100C or < -50C)
    # This acts as a basic filter for sensor errors.
    original_len = len(df_clean)
    df_clean = df_clean[(df_clean['Temperature_C'] > -20) & (df_clean['Temperature_C'] < 60)]
    df_clean = df_clean[df_clean['Humidity_Pct'].between(0, 100)]
    print(f"Noise removal: Dropped {original_len - len(df_clean)} rows based on physical limits.")

    # 4. Encoding Categorical Labels
    # The target 'Odour_Severity' is text (Good, Moderate, etc.). Models need numbers.
    print("Encoding categorical labels...")
    label_encoder = LabelEncoder()
    # Fit transform converts 'Good', 'Moderate', etc. to 0, 1, 2, 3
    df_clean['Odour_Severity_Encoded'] = label_encoder.fit_transform(df_clean['Odour_Severity'])
    
    # Store the mapping for reference
    mapping = dict(zip(label_encoder.classes_, label_encoder.transform(label_encoder.classes_)))
    print(f"Label Mapping: {mapping}")

    # Separate Features (X) and Target (y)
    X = df_clean[['Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour']]
    y = df_clean['Odour_Severity_Encoded']

    # 5. Normalizing Numerical Features
    # Sensors have different scales (e.g. Ohms in thousands, Temp in tens). 
    # Normalization prevents one features from dominating distance-based calculations.
    # We use StandardScaler for (mean=0, variance=1)
    print("Normalizing features...")
    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X)
    X_scaled_df = pd.DataFrame(X_scaled, columns=X.columns)

    # 6. Split into Training and Testing Sets
    print("Splitting data...")
    X_train, X_test, y_train, y_test = train_test_split(X_scaled_df, y, test_size=0.2, random_state=42)

    print(f"Training Set: {X_train.shape}")
    print(f"Testing Set: {X_test.shape}")

    # Save processed subsets for inspection
    # combine X and y for saving
    train_output = X_train.copy()
    train_output['Target'] = y_train
    test_output = X_test.copy()
    test_output['Target'] = y_test

    train_output.to_csv(processed_train_path, index=False)
    test_output.to_csv(processed_test_path, index=False)
    print(f"Saved processed data to {processed_train_path} and {processed_test_path}")

if __name__ == "__main__":
    preprocess_data()
