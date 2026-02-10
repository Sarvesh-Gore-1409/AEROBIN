package com.aerobins.backend.repository;

import com.aerobins.backend.entity.DailyAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsRepository extends JpaRepository<DailyAnalytics, Long> {
    List<DailyAnalytics> findAllByOrderByDateAsc();
}
