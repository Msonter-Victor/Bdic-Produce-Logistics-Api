package dev.gagnon.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/health")
@Slf4j
public class HealthController {
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        try {
            return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "CALL-SERVICE",
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage(), e);
            return ResponseEntity.status(503).body(Map.of(
                "status", "DOWN",
                "service", "CALL-SERVICE",
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
}