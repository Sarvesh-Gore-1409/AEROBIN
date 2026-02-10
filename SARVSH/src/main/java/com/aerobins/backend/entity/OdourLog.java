package com.aerobins.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "odour_logs")
public class OdourLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dustbin_id")
    private Long dustbinId;

    private Double temperature;
    private Double humidity;
    private Double gasResistance;
    private Double vocIndex;
    private Double fillLevel;

    @Column(name = "predicted_severity")
    private String predictedSeverity;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }

    public OdourLog() {
    }

    public OdourLog(Long dustbinId, Double temperature, Double humidity, Double gasResistance, Double vocIndex,
            Double fillLevel, String predictedSeverity, Double confidenceScore, String explanation) {
        this.dustbinId = dustbinId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.gasResistance = gasResistance;
        this.vocIndex = vocIndex;
        this.fillLevel = fillLevel;
        this.predictedSeverity = predictedSeverity;
        this.confidenceScore = confidenceScore;
        this.explanation = explanation;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDustbinId() {
        return dustbinId;
    }

    public void setDustbinId(Long dustbinId) {
        this.dustbinId = dustbinId;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getGasResistance() {
        return gasResistance;
    }

    public void setGasResistance(Double gasResistance) {
        this.gasResistance = gasResistance;
    }

    public Double getVocIndex() {
        return vocIndex;
    }

    public void setVocIndex(Double vocIndex) {
        this.vocIndex = vocIndex;
    }

    public Double getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(Double fillLevel) {
        this.fillLevel = fillLevel;
    }

    public String getPredictedSeverity() {
        return predictedSeverity;
    }

    public void setPredictedSeverity(String predictedSeverity) {
        this.predictedSeverity = predictedSeverity;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
