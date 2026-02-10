
from flask import Flask, request, jsonify
import pandas as pd
import joblib
import os
import sys

app = Flask(__name__)

# Define paths
MODEL_PATH = os.path.join(os.path.dirname(__file__), '..', 'datasets', 'odour_model.pkl')

# Global model variable
model = None

def load_model():
    global model
    if os.path.exists(MODEL_PATH):
        try:
            model = joblib.load(MODEL_PATH)
            print(f"Model loaded successfully from {MODEL_PATH}")
        except Exception as e:
            print(f"Error loading model: {e}")
            sys.exit(1)
    else:
        print(f"Model file not found at {MODEL_PATH}")
        sys.exit(1)

def add_derived_features(df):
    """
    Adds engineered features to consistent with training.
    """
    # 1. Heat Interaction (Temp * Humidity)
    df['Heat_Interaction'] = (df['Temperature_C'] * df['Humidity_Pct']) / 100.0

    # 2. VOC Impact (VOC * Temp)
    # We emphasize this feature
    df['VOC_Temp_Interaction'] = (df['VOC_Index'] * df['Temperature_C']) / 100.0
    
    return df

def generate_explanation(model, df, feature_names, prediction):
    # 1. Get Global Feature Importance (from Random Forest)
    importances = model.feature_importances_
    feature_importance_dict = dict(zip(feature_names, importances))
    
    # Sort by importance
    sorted_importance = sorted(feature_importance_dict.items(), key=lambda x: x[1], reverse=True)
    top_3_features = sorted_importance[:3]
    
    # 2. Heuristic Contextual Explainability (Mocking 'Why' based on input values)
    # Since we can't easily do local SHAP without high latency/dependencies, we use rule-based mapping combined with global importance.
    
    narrative_parts = []
    
    # Check Temperature
    temp = df['Temperature_C'].values[0]
    if temp > 30:
        narrative_parts.append("High temperature accelerates decomposition.")
    
    # Check Humidity
    humidity = df['Humidity_Pct'].values[0]
    if humidity > 70:
        narrative_parts.append("High humidity traps odours.")
        
    # Check Fill Level
    fill = df['Fill_Level_Pct'].values[0]
    if fill > 80:
        narrative_parts.append("Critical fill level concentrates emitted gases.")
        
    # Check Gas Resistance (Low resistance = High concentration)
    gas = df['Gas_Resistance_Ohms'].values[0]
    if gas < 50000: # Assuming 50k is a threshold for 'smelly'
        narrative_parts.append("Gas sensors detect significant volatile compounds.")
        
    narrative = " ".join(narrative_parts)
    if not narrative:
        narrative = "Standard environmental conditions detected."

    return {
        'top_features': {k: float(f"{v:.4f}") for k, v in top_3_features},
        'primary_driver': top_3_features[0][0],
        'narrative': narrative
    }

@app.route('/predict', methods=['POST'])
def predict():
    if model is None:
        return jsonify({'error': 'Model not loaded'}), 500

    try:
        data = request.get_json()
        
        # Validate input keys
        required_keys = ['Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour']
        if not all(key in data for key in required_keys):
            return jsonify({'error': f'Missing required keys. Expected: {required_keys}'}), 400

        # Create DataFrame for prediction
        # We start with raw values
        features = {
            'Temperature_C': float(data['Temperature_C']),
            'Humidity_Pct': float(data['Humidity_Pct']),
            'Gas_Resistance_Ohms': float(data['Gas_Resistance_Ohms']),
            'VOC_Index': float(data['VOC_Index']),
            'Fill_Level_Pct': float(data['Fill_Level_Pct']),
            'Hour': float(data['Hour'])
        }
        
        df = pd.DataFrame([features])

        # Feature Engineering
        df = add_derived_features(df)
        
        # Ensure correct column order for model
        final_features = [
            'Temperature_C', 'Humidity_Pct', 'Gas_Resistance_Ohms', 'VOC_Index', 'Fill_Level_Pct', 'Hour',
            'Heat_Interaction', 'VOC_Temp_Interaction'
        ]
        
        # Select and order columns
        df_final = df[final_features]

        # Predict
        prediction = model.predict(df_final)[0]
        
        # Get probabilities (confidence)
        # partial_proba returns array of shape (1, n_classes)
        probabilities = model.predict_proba(df_final)[0]
        
        # Get confidence of the predicted class
        class_index = list(model.classes_).index(prediction)
        confidence = probabilities[class_index]

        return jsonify({
            'odour_severity': prediction,
            'confidence_score': float(f"{confidence:.4f}"),
            'status': 'success',
            'explanation': generate_explanation(model, df_final, final_features, prediction)
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500

@app.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'online', 'model_loaded': model is not None})

if __name__ == '__main__':
    load_model()
    # Run slightly different port to avoid conflicts, commonly developers use 5000 or 8080.
    # We'll use 5000 as default Flask port.
    app.run(host='0.0.0.0', port=5000, debug=False)
