
import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import classification_report, accuracy_score, confusion_matrix
import joblib
import os

# Define paths
dataset_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'odour_prediction_dataset.csv')
model_path = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'odour_model.pkl')

def add_derived_features(df):
    """
    Adds engineered features to improve model context.
    Must be consistent between training and prediction.
    """
    # 1. Heat Interaction (Temp * Humidity)
    df['Heat_Interaction'] = (df['Temperature_C'] * df['Humidity_Pct']) / 100.0

    # 2. VOC Impact (VOC * Temp)
    # We emphasize this feature
    df['VOC_Temp_Interaction'] = (df['VOC_Index'] * df['Temperature_C']) / 100.0
    
    return df

def train_model():
    print("Loading dataset...")
    try:
        df = pd.read_csv(dataset_path)
    except FileNotFoundError:
        print(f"Error: Dataset not found at {dataset_path}")
        return

    # Preprocessing
    print("Preprocessing data and Engineering Features...")
    df['Timestamp'] = pd.to_datetime(df['Timestamp'])
    df['Hour'] = df['Timestamp'].dt.hour
    
    # Apply Feature Engineering
    df = add_derived_features(df)
    
    # Defined Features
    feature_names = [
        'Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour',
        'Heat_Interaction', 'VOC_Temp_Interaction'
    ]
    
    X = df[feature_names]
    y = df['Odour_Severity']

    # Split data
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)

    # Train Random Forest Classifier
    # Reverting to standard robust parameters. GridSearch on N=30 is unstable.
    # n_estimators=100 is standard. 
    # class_weight=None (Standard) ensures we don't skew too much against majority/minority artificially absent data.
    print(f"\nTraining Random Forest Classifier (Robust Config)...")
    clf = RandomForestClassifier(n_estimators=100, random_state=42)
    clf.fit(X_train, y_train)

    # Evaluation
    print("\nEvaluating model...")
    y_pred = clf.predict(X_test)
    
    accuracy = accuracy_score(y_test, y_pred)
    print(f"Model Accuracy: {accuracy * 100:.2f}%")
    
    print("\nClassification Report:")
    print(classification_report(y_test, y_pred))
    
    print("\nConfusion Matrix:")
    print(confusion_matrix(y_test, y_pred, labels=clf.classes_))
    
    # Feature Importance
    print("\nFeature Importance:")
    importances = clf.feature_importances_
    feature_imp_df = pd.DataFrame({'Feature': feature_names, 'Importance': importances})
    feature_imp_df = feature_imp_df.sort_values(by='Importance', ascending=False)
    feature_imp_df['Importance'] = feature_imp_df['Importance'].apply(lambda x: f"{x*100:.2f}%")
    print(feature_imp_df.to_string(index=False))

    # Save Model
    print(f"\nSaving model to {model_path}...")
    joblib.dump(clf, model_path)
    print("Done!")

if __name__ == "__main__":
    train_model()
