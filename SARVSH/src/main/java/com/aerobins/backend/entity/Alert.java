package com.aerobins.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dustbin_id", nullable = false)
    private Dustbin dustbin;

    @Column(name = "alert_type", nullable = false)
    private String alertType; // 'Odour', 'Fill-Level', 'Methane', 'Combined'

    @Column(name = "alert_message", columnDefinition = "TEXT")
    private String alertMessage;

    @Column(name = "severity_level")
    private Integer severityLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    private String status; // 'Pending', 'Resolved'

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null)
            status = "Pending";
        if (severityLevel == null)
            severityLevel = 1;
    }

    public Alert() {
    }

    public Alert(Dustbin dustbin, String alertType, String alertMessage, Integer severityLevel, String status) {
        this.dustbin = dustbin;
        this.alertType = alertType;
        this.alertMessage = alertMessage;
        this.severityLevel = severityLevel;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dustbin getDustbin() {
        return dustbin;
    }

    public void setDustbin(Dustbin dustbin) {
        this.dustbin = dustbin;
    }

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public Integer getSeverityLevel() {
        return severityLevel;
    }

    public void setSeverityLevel(Integer severityLevel) {
        this.severityLevel = severityLevel;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
