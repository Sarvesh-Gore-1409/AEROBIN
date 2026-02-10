package com.aerobins.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dustbins_live")
public class Dustbin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dustbin_id")
    private Long id;

    @Column(name = "bin_id", nullable = false, unique = true)
    private String binId;

    @Column(name = "location_name", nullable = false)
    private String location;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Integer fillLevel; // 0-100 percentage

    @Column(nullable = false)
    private Double odorLevel; // 0-10 scale

    @Column(nullable = false)
    private String status; // ACTIVE, MAINTENANCE, FULL

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "odour_severity")
    private String odourSeverity;

    @Column(name = "predicted_worsen_time")
    private String predictedWorsenTime;

    @Column(name = "priority_level")
    private String priorityLevel;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructors
    public Dustbin() {
        this.createdAt = LocalDateTime.now();
        this.lastUpdated = LocalDateTime.now();
    }

    public Dustbin(String binId, String location, Double latitude, Double longitude) {
        this();
        this.binId = binId;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fillLevel = 0;
        this.odorLevel = 0.0;
        this.status = "ACTIVE";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBinId() {
        return binId;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getFillLevel() {
        return fillLevel;
    }

    public void setFillLevel(Integer fillLevel) {
        this.fillLevel = fillLevel;
        this.lastUpdated = LocalDateTime.now();
    }

    public Double getOdorLevel() {
        return odorLevel;
    }

    public void setOdorLevel(Double odorLevel) {
        this.odorLevel = odorLevel;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOdourSeverity() {
        return odourSeverity;
    }

    public void setOdourSeverity(String odourSeverity) {
        this.odourSeverity = odourSeverity;
    }

    public String getPredictedWorsenTime() {
        return predictedWorsenTime;
    }

    public void setPredictedWorsenTime(String predictedWorsenTime) {
        this.predictedWorsenTime = predictedWorsenTime;
    }

    public String getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
}
