package com.aerobins.backend.controller;

import com.aerobins.backend.entity.LoginActivity;
import com.aerobins.backend.service.LoginActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private LoginActivityService loginActivityService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> login(@RequestBody @NonNull LoginRequest request,
            HttpServletRequest httpRequest) {
        try {
            String username = request.username != null ? request.username.trim() : "";
            String password = request.password != null ? request.password : "";

            // Validate input
            if (username.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Username is required"));
            }

            if (password == null || password.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(createErrorResponse("Password is required"));
            }

            // Extract client information
            String userAgent = httpRequest.getHeader("User-Agent");
            if (userAgent == null) {
                userAgent = "";
            }
            String ipAddress = extractClientIp(httpRequest);

            // Simple credential validation - accept any non-empty username and password
            // For demo purposes, any non-empty credentials will be accepted
            boolean isValid = !username.isEmpty() && !password.isEmpty();

            System.out.println("Login attempt - Username: " + username + ", Password length: "
                    + (password != null ? password.length() : 0) + ", Valid: " + isValid);

            // ALWAYS log the login attempt to database (both success and failure)
            LoginActivity activity = null;
            System.out.println("=== ATTEMPTING TO SAVE LOGIN ACTIVITY ===");
            System.out.println("Username: " + username);

            activity = loginActivityService.logActivity(
                    username,
                    password,
                    isValid,
                    userAgent,
                    ipAddress);

            if (activity != null && activity.getId() != null) {
                System.out.println("✓✓✓ SUCCESS: Login activity saved to database!");
                System.out.println("   - ID: " + activity.getId());
            } else {
                System.err.println("✗✗✗ ERROR: Activity saved but ID is null!");
                throw new RuntimeException("Database save failed - ID is null");
            }

            // Prepare response
            Map<String, Object> response = new HashMap<>();
            if (isValid) {
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("username", username);
                if (activity != null) {
                    response.put("activityId", activity.getId());
                }
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Invalid username or password");
                if (activity != null) {
                    response.put("activityId", activity.getId());
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            // Handle any unexpected errors
            System.err.println("ERROR in login controller: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = createErrorResponse(
                    "An error occurred during login. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    public static class LoginRequest {
        @NonNull
        public String username = "";
        public String password = "";
    }
}
