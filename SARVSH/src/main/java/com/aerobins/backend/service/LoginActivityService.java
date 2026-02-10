package com.aerobins.backend.service;

import com.aerobins.backend.entity.LoginActivity;
import com.aerobins.backend.repository.LoginActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class LoginActivityService {

    @Autowired
    private LoginActivityRepository repository;

    @Transactional
    public LoginActivity logActivity(@NonNull String username, @Nullable String password, boolean success,
            @Nullable String userAgent, @Nullable String ipAddress) {
        try {
            LoginActivity activity = new LoginActivity();
            activity.setUsername(username);
            activity.setPassword(password != null ? password : "");
            activity.setSuccess(success);
            activity.setUserAgent(userAgent != null ? userAgent : "");
            activity.setIpAddress(ipAddress != null ? ipAddress : "");
            activity.setLoginTime(LocalDateTime.now());

            // Save and flush to ensure immediate persistence
            LoginActivity saved = repository.saveAndFlush(activity);

            // Verify the save was successful
            if (saved == null || saved.getId() == null) {
                throw new RuntimeException("Failed to save login activity - returned null or no ID");
            }

            // Extract ID and assert non-null to satisfy @NonNull requirement
            Long id = Objects.requireNonNull(saved.getId(), "Login activity ID must not be null");

            // Double-check by querying the database
            LoginActivity verified = repository.findById(id).orElse(null);
            if (verified == null) {
                throw new RuntimeException("Login activity saved but not found in database!");
            }

            System.out.println("✓✓✓ DATABASE SAVE VERIFIED - ID: " + saved.getId() + ", Username: "
                    + saved.getUsername() + ", Success: " + saved.getSuccess());
            return saved;
        } catch (Exception e) {
            System.err.println("✗✗✗ DATABASE SAVE FAILED: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let controller handle it
        }
    }

    public List<LoginActivity> getAllActivities() {
        return repository.findAll();
    }

    public List<LoginActivity> getActivitiesByUsername(@NonNull String username) {
        return repository.findByUsernameOrderByLoginTimeDesc(username);
    }
}
