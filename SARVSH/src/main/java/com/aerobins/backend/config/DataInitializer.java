package com.aerobins.backend.config;

import com.aerobins.backend.entity.Dustbin;
import com.aerobins.backend.repository.DustbinRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DustbinRepository dustbinRepository;

    @Autowired
    private com.aerobins.backend.repository.RouteRepository routeRepository;

    @Autowired
    private com.aerobins.backend.repository.AlertRepository alertRepository;

    @Autowired
    private com.aerobins.backend.repository.AnalyticsRepository analyticsRepository;

    @Override
    public void run(String... args) throws Exception {
        if (dustbinRepository.count() == 0) {
            System.out.println("Initializing Database with Mock Dustbins...");

            createBin("BIN-001", "Connaught Place, Zone 1", 28.6139, 77.2090, 45, 2.5, "ACTIVE");
            createBin("BIN-002", "Karol Bagh Market", 28.6600, 77.1900, 92, 8.5, "FULL"); // Critical
            createBin("BIN-003", "Lajpat Nagar Central", 28.5600, 77.2400, 67, 5.0, "ACTIVE");
            createBin("BIN-004", "Chandni Chowk Main", 28.6506, 77.2300, 89, 7.8, "MAINTENANCE"); // Critical/High
            createBin("BIN-005", "Sarojini Nagar Market", 28.5700, 77.1900, 30, 1.2, "ACTIVE");
            createBin("BIN-006", "India Gate Area", 28.6100, 77.2300, 78, 6.5, "ACTIVE"); // High Priority
            createBin("BIN-007", "Hauz Khas Village", 28.5500, 77.1900, 15, 0.8, "ACTIVE");

            System.out.println("Database initialization completed.");
        }

        if (routeRepository.count() == 0) {
            System.out.println("Initializing Database with Mock Routes...");

            // Vehicles for Critical/High Bins
            createRoute("BIN-002", "[{\"lat\": 28.6600, \"lng\": 77.1900}, {\"lat\": 28.6500, \"lng\": 77.1800}]",
                    new java.math.BigDecimal("5.200"), 15);
            createRoute("BIN-004", "[{\"lat\": 28.6506, \"lng\": 77.2300}, {\"lat\": 28.6400, \"lng\": 77.2200}]",
                    new java.math.BigDecimal("8.100"), 25);
            createRoute("BIN-006", "[{\"lat\": 28.6100, \"lng\": 77.2300}, {\"lat\": 28.6000, \"lng\": 77.2200}]",
                    new java.math.BigDecimal("3.500"), 12);

            System.out.println("Route initialization completed.");
        }

        if (alertRepository.count() == 0) {
            System.out.println("Initializing Database with Mock Alerts...");

            createAlert("BIN-004", "Fill-Level",
                    "Chandni Chowk Main dustbin is 92% full - Immediate attention required", 3, "Pending");
            createAlert("BIN-002", "Odour", "Karol Bagh Market showing poor air quality readings", 2, "Pending");
            createAlert("BIN-006", "Combined", "India Gate Area dustbin due for routine maintenance", 1, "Pending");

            System.out.println("Alert initialization completed.");
        }

        if (analyticsRepository.count() == 0) {
            System.out.println("Initializing Database with Mock Analytics...");
            createAnalyticsDaily(java.time.LocalDate.now().minusDays(6), 78.0, 88.0, 120);
            createAnalyticsDaily(java.time.LocalDate.now().minusDays(5), 82.0, 90.0, 135);
            createAnalyticsDaily(java.time.LocalDate.now().minusDays(4), 85.0, 91.0, 140);
            createAnalyticsDaily(java.time.LocalDate.now().minusDays(3), 80.0, 89.0, 125);
            createAnalyticsDaily(java.time.LocalDate.now().minusDays(2), 88.0, 93.0, 150);
            createAnalyticsDaily(java.time.LocalDate.now().minusDays(1), 90.0, 94.0, 155);
            createAnalyticsDaily(java.time.LocalDate.now(), 85.0, 92.0, 110);
            System.out.println("Analytics initialization completed.");
        }
    }

    private void createAnalyticsDaily(java.time.LocalDate date, Double eff, Double acc, Integer count) {
        com.aerobins.backend.entity.DailyAnalytics da = new com.aerobins.backend.entity.DailyAnalytics(date, eff, acc,
                count);
        analyticsRepository.save(da);
    }

    private void createBin(String binId, String location, double lat, double lng, int fill, double odor,
            String status) {
        Dustbin bin = new Dustbin(binId, location, lat, lng);
        bin.setFillLevel(fill);
        bin.setOdorLevel(odor);
        bin.setStatus(status);
        dustbinRepository.save(bin);
    }

    private void createRoute(String binId, String jsonPath, java.math.BigDecimal dist, int time) {
        java.util.Optional<Dustbin> binOpt = dustbinRepository.findByBinId(binId);
        if (binOpt.isPresent()) {
            com.aerobins.backend.entity.Route r = new com.aerobins.backend.entity.Route(binOpt.get(), jsonPath, dist,
                    time);
            routeRepository.save(r);
        }
    }

    private void createAlert(String binId, String type, String msg, int severity, String status) {
        java.util.Optional<Dustbin> binOpt = dustbinRepository.findByBinId(binId);
        if (binOpt.isPresent()) {
            com.aerobins.backend.entity.Alert a = new com.aerobins.backend.entity.Alert(binOpt.get(), type, msg,
                    severity, status);
            alertRepository.save(a);
        }
    }
}
