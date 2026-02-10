package com.aerobins.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private Environment environment;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Serve static files from src/main/resources/static/
        // Note: Controller mappings (like /api/**) take precedence over static resource handlers
        // This configuration is optional as Spring Boot serves static resources by default
        var resourceHandler = registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
        
        // Only disable caching in development mode
        // In production, use default caching (1 year) for better performance
        String[] activeProfiles = environment.getActiveProfiles();
        boolean isProduction = Arrays.stream(activeProfiles)
                .anyMatch(profile -> profile.equalsIgnoreCase("prod") || profile.equalsIgnoreCase("production"));
        
        if (!isProduction) {
            // Development mode: disable caching for easier debugging
            resourceHandler.setCachePeriod(0);
        }
        // Production mode: cache period defaults to 1 year (31536000 seconds) for optimal performance
    }
}
