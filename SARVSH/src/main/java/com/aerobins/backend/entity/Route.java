package com.aerobins.backend.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "route_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dustbin_id", nullable = false)
    private Dustbin dustbin;

    @Column(name = "optimized_path_json", columnDefinition = "TEXT")
    private String optimizedPathJson;

    @Column(name = "distance_km", precision = 9, scale = 3)
    private BigDecimal distanceKm;

    @Column(name = "estimated_time_min")
    private Integer estimatedTimeMin;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }

    public Route() {
    }

    public Route(Dustbin dustbin, String optimizedPathJson, BigDecimal distanceKm, Integer estimatedTimeMin) {
        this.dustbin = dustbin;
        this.optimizedPathJson = optimizedPathJson;
        this.distanceKm = distanceKm;
        this.estimatedTimeMin = estimatedTimeMin;
    }

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

    public String getOptimizedPathJson() {
        return optimizedPathJson;
    }

    public void setOptimizedPathJson(String optimizedPathJson) {
        this.optimizedPathJson = optimizedPathJson;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Integer getEstimatedTimeMin() {
        return estimatedTimeMin;
    }

    public void setEstimatedTimeMin(Integer estimatedTimeMin) {
        this.estimatedTimeMin = estimatedTimeMin;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}
