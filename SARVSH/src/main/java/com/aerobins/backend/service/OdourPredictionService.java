package com.aerobins.backend.service;

import com.aerobins.backend.entity.Alert;
import com.aerobins.backend.entity.Dustbin;
import com.aerobins.backend.entity.OdourLog;
import com.aerobins.backend.repository.AlertRepository;
import com.aerobins.backend.repository.DustbinRepository;
import com.aerobins.backend.repository.OdourLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OdourPredictionService {

    @Autowired
    private OdourLogRepository odourLogRepository;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private DustbinRepository dustbinRepository;

    private final String API_URL = "http://localhost:5000/predict";
    private final RestTemplate restTemplate = new RestTemplate();

    public String predictOdour(double temperature, double humidity, double gasResistance, double vocIndex,
            double fillLevel, int hour) {
        return predictOdour(temperature, humidity, gasResistance, vocIndex, fillLevel, hour, null);
    }

    public String predictOdour(double temperature, double humidity, double gasResistance, double vocIndex,
            double fillLevel, int hour, Long dustbinId) {
        try {
            // Prepare Request
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("Temperature_C", temperature);
            requestMap.put("Humidity_Pct", humidity);
            requestMap.put("Gas_Resistance_Ohms", gasResistance);
            requestMap.put("VOC_Index", vocIndex);
            requestMap.put("Fill_Level_Pct", fillLevel);
            requestMap.put("Hour", hour);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestMap, headers);

            // Call API
            ResponseEntity<Map> response = restTemplate.postForEntity(API_URL, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && "success".equals(body.get("status"))) {
                String severity = (String) body.get("odour_severity");
                Double confidence = ((Number) body.get("confidence_score")).doubleValue();

                // Extract Explanation
                String explanation = "";
                if (body.containsKey("explanation")) {
                    Map<String, Object> explanationMap = (Map<String, Object>) body.get("explanation");
                    explanation = (String) explanationMap.get("narrative");
                    // Can also extract primary_driver if needed, but narrative is summarized
                }

                // Save Log if dustbinId is provided
                if (dustbinId != null) {
                    OdourLog log = new OdourLog(dustbinId, temperature, humidity, gasResistance, vocIndex, fillLevel,
                            severity, confidence, explanation);
                    odourLogRepository.save(log);

                    // Evaluate Alert Rules based on Odour and Fill Level
                    evaluateAndCreateAlert(dustbinId, severity, fillLevel, confidence);
                }

                return severity;
            } else {
                return "Error: API returned failure or invalid response";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling prediction model: " + e.getMessage();
        }
    }

    private void evaluateAndCreateAlert(Long dustbinId, String odourSeverity, Double fillLevel, Double confidence) {
        Dustbin dustbin = dustbinRepository.findById(dustbinId).orElse(null);
        if (dustbin == null)
            return;

        int priorityLevel = 0;
        String alertMessage = "";
        String alertType = "Routine";

        boolean isSevereOdour = "Severe".equalsIgnoreCase(odourSeverity);
        boolean isVeryHighOdour = "Very High".equalsIgnoreCase(odourSeverity);
        boolean isHighOdour = "High".equalsIgnoreCase(odourSeverity);
        boolean isModerateOdour = "Moderate".equalsIgnoreCase(odourSeverity);

        // --- Alert Logic Matrix for Nagar Nigam Operations ---

        // Priority 4: CRITICAL (Immediate Action - < 1 Hour)
        // Trigger: Severe Odour OR (Very High Odour + Fill > 70%) OR Fill > 95%
        if (isSevereOdour || (isVeryHighOdour && fillLevel >= 70.0) || fillLevel >= 95.0) {
            priorityLevel = 4;
            alertType = "CRITICAL_RISK";
            if (fillLevel >= 95.0) {
                alertMessage = "CRITICAL: Bin Overflow Imminent (" + fillLevel + "%). Immediate collection required.";
            } else {
                alertMessage = "CRITICAL: Severe bacterial activity detected. Public health risk. Collect IMMEDIATELY.";
            }
        }
        // Priority 3: HIGH (Urgent - Within 4 Hours)
        // Trigger: Very High Odour OR (High Odour + Fill > 50%) OR Fill > 80%
        else if (isVeryHighOdour || (isHighOdour && fillLevel >= 50.0) || fillLevel >= 80.0) {
            priorityLevel = 3;
            alertType = "URGENT_ATTENTION";
            alertMessage = "Alert: High odour/fill levels. Schedule pickup within 4 hours to prevent complaints.";
        }
        // Priority 2: MEDIUM (Plan for Today)
        // Trigger: High Odour OR (Moderate Odour + Fill > 60%) OR Fill > 60%
        else if (isHighOdour || (isModerateOdour && fillLevel >= 60.0) || fillLevel >= 60.0) {
            priorityLevel = 2;
            alertType = "MAINTENANCE_REQUIRED";
            alertMessage = "Warning: Elevated levels detected. Add to current daily route.";
        }

        // Save Alert if Threshold Met (Level 2+)
        if (priorityLevel >= 2) {
            final int finalPriorityLevel = priorityLevel;
            // Check for existing pending alerts to avoid spamming the dashboard
            boolean hasPending = alertRepository.findAll().stream()
                    .anyMatch(a -> a.getDustbin().getId().equals(dustbinId) && "Pending".equals(a.getStatus())
                            && a.getSeverityLevel() >= finalPriorityLevel);

            if (!hasPending) {
                Alert alert = new Alert();
                alert.setDustbin(dustbin);
                alert.setAlertType("Smart-" + alertType);
                alert.setSeverityLevel(priorityLevel);
                alert.setAlertMessage(alertMessage + " [Odour: " + odourSeverity + ", Fill: " + fillLevel + "%]");
                alert.setStatus("Pending");
                alertRepository.save(alert);
            }
        }

        // UPDATE DUSTBIN ENTITY FOR DASHBOARD
        // Mapping Priority to String
        String priorityStr = "Low";
        if (priorityLevel == 2)
            priorityStr = "Medium";
        if (priorityLevel == 3)
            priorityStr = "High";
        if (priorityLevel == 4)
            priorityStr = "Critical";

        // Predicting Time to Worsen (Heuristic)
        String timeToWorsen = "Safe";
        if (priorityLevel == 4)
            timeToWorsen = "< 1 Hour";
        else if (priorityLevel == 3)
            timeToWorsen = "2-4 Hours";
        else if (priorityLevel == 2)
            timeToWorsen = "~12 Hours";
        else if (fillLevel > 40)
            timeToWorsen = "1-2 Days";

        dustbin.setOdourSeverity(odourSeverity); // "Moderate", "High", etc.
        dustbin.setPriorityLevel(priorityStr);
        dustbin.setPredictedWorsenTime(timeToWorsen);
        dustbinRepository.save(dustbin);
    }
}
