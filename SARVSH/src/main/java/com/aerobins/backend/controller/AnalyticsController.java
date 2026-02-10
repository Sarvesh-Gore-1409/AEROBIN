package com.aerobins.backend.controller;

import com.aerobins.backend.entity.DailyAnalytics;
import com.aerobins.backend.repository.AnalyticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsRepository analyticsRepository;

    @GetMapping
    public Map<String, Object> getAnalytics() {
        List<DailyAnalytics> history = analyticsRepository.findAllByOrderByDateAsc();

        // Calculate averages for the "Hero" cards
        double avgEff = history.stream().mapToDouble(DailyAnalytics::getCollectionEfficiency).average().orElse(0.0);
        double avgAcc = history.stream().mapToDouble(DailyAnalytics::getOdourAccuracy).average().orElse(0.0);

        Map<String, Object> response = new HashMap<>();
        response.put("efficiency", (int) Math.round(avgEff));
        response.put("accuracy", (int) Math.round(avgAcc));
        response.put("trends", history);

        return response;
    }
}
