package dev.gagnon.Controller;

import dev.gagnon.Service.PromotionExpiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionExpiryController {

    private final PromotionExpiryService promotionExpiryService;

    /**
     * Manually trigger promotion expiry check
     * This endpoint can be used by admins to force check promotions
     */
    @PostMapping("/expire")
    public ResponseEntity<String> manualExpirePromotions() {
        try {
            promotionExpiryService.manualExpirePromotions();
            return ResponseEntity.ok("Promotion expiry check completed successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error during promotion expiry check: " + e.getMessage());
        }
    }

    /**
     * Get current promotion statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<PromotionExpiryService.PromotionStats> getPromotionStats() {
        try {
            PromotionExpiryService.PromotionStats stats = promotionExpiryService.getPromotionStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint for the promotion service
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Promotion Expiry Service is running");
    }
}