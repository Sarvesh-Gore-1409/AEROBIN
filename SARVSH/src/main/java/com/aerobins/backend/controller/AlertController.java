package com.aerobins.backend.controller;

import com.aerobins.backend.entity.Alert;
import com.aerobins.backend.repository.AlertRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;

    @GetMapping
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @PutMapping("/{id}/resolve")
    public Alert resolveAlert(@PathVariable @NonNull Long id) {
        return alertRepository.findById(id).map(alert -> {
            alert.setStatus("Resolved");
            return alertRepository.save(alert);
        }).orElseThrow(() -> new RuntimeException("Alert not found"));
    }
}
