package com.aerobins.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_analytics")
public class DailyAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "collection_efficiency")
    private Double collectionEfficiency; // Percentage 0-100

    @Column(name = "odour_accuracy")
    private Double odourAccuracy; // Percentage 0-100

    @Column(name = "collections_completed")
    private Integer collectionsCompleted;

    public DailyAnalytics() {
    }

    public DailyAnalytics(LocalDate date, Double collectionEfficiency, Double odourAccuracy,
            Integer collectionsCompleted) {
        this.date = date;
        this.collectionEfficiency = collectionEfficiency;
        this.odourAccuracy = odourAccuracy;
        this.collectionsCompleted = collectionsCompleted;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getCollectionEfficiency() {
        return collectionEfficiency;
    }

    public void setCollectionEfficiency(Double collectionEfficiency) {
        this.collectionEfficiency = collectionEfficiency;
    }

    public Double getOdourAccuracy() {
        return odourAccuracy;
    }

    public void setOdourAccuracy(Double odourAccuracy) {
        this.odourAccuracy = odourAccuracy;
    }

    public Integer getCollectionsCompleted() {
        return collectionsCompleted;
    }

    public void setCollectionsCompleted(Integer collectionsCompleted) {
        this.collectionsCompleted = collectionsCompleted;
    }
}
