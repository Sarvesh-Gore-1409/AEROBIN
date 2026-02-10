package com.aerobins.backend.controller;

import com.aerobins.backend.entity.LoginActivity;
import com.aerobins.backend.service.LoginActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/login-activity")
@CrossOrigin(origins = "*")
public class LoginActivityController {

    @Autowired
    private LoginActivityService service;

    @PostMapping
    public ResponseEntity<LoginActivity> log(@RequestBody @NonNull LoginActivityRequest request,
            HttpServletRequest httpRequest) {
        String username = request.username;
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        username = username.trim();
        boolean success = Boolean.TRUE.equals(request.success);
        String userAgent = request.userAgent;
        if (userAgent == null) {
            userAgent = httpRequest.getHeader("User-Agent");
            if (userAgent == null) {
                userAgent = "";
            }
        }
        String ipAddress = extractClientIp(httpRequest);
        LoginActivity saved = service.logActivity(Objects.requireNonNull(username), null, success, userAgent,
                ipAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<java.util.List<LoginActivity>> getAllActivities() {
        return ResponseEntity.ok(service.getAllActivities());
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyDatabase() {
        Map<String, Object> status = new HashMap<>();
        try {
            List<LoginActivity> allActivities = service.getAllActivities();
            status.put("status", "connected");
            status.put("tableExists", true);
            status.put("recordCount", allActivities.size());
            status.put("message", "Database connection successful");

            // Test save/retrieve
            try {
                LoginActivity testActivity = service.logActivity(
                        "VERIFY_TEST_" + System.currentTimeMillis(),
                        null,
                        true,
                        "Database Verification Test",
                        "127.0.0.1");
                status.put("testSave", "success");
                status.put("testRecordId", testActivity.getId());
            } catch (Exception e) {
                status.put("testSave", "failed");
                status.put("testSaveError", e.getMessage());
            }
        } catch (Exception e) {
            status.put("status", "error");
            status.put("tableExists", false);
            status.put("message", "Database connection failed: " + e.getMessage());
            status.put("error", e.getClass().getSimpleName());
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/{username}")
    public ResponseEntity<java.util.List<LoginActivity>> getActivitiesByUsername(@PathVariable String username) {
        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String trimmedUsername = Objects.requireNonNull(username.trim());
        return ResponseEntity.ok(service.getActivitiesByUsername(trimmedUsername));
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            int comma = forwarded.indexOf(',');
            return comma > 0 ? forwarded.substring(0, comma).trim() : forwarded.trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isEmpty()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }

    public static class LoginActivityRequest {
        @NonNull
        public String username = "";
        @Nullable
        public Boolean success;
        @Nullable
        public String userAgent;
    }
}
