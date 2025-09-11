package dev.gagnon.Service;

import dev.gagnon.Model.Product;
import dev.gagnon.Model.Shop;
import dev.gagnon.Model.constants.FeaturedStatus;
import dev.gagnon.Model.constants.PromotedStatus;
import dev.gagnon.Repository.ProductRepository;
import dev.gagnon.Repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionExpiryService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    /**
     * Runs daily at midnight to check and expire promotions
     */
    @Scheduled(cron = "0 0 0 * * *") // Runs at midnight every day
    @Transactional
    public void expirePromotions() {
        log.info("Starting promotion expiry check at {}", LocalDateTime.now());
        
        try {
            expireShopPromotions();
            expireProductPromotions();
            expireProductFeatures();
            expireProductFloats();
            
            log.info("Promotion expiry check completed successfully at {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("Error during promotion expiry check: {}", e.getMessage(), e);
        }
    }

    /**
     * Expire shop promotions that are older than 2 weeks (14 days)
     */
    private void expireShopPromotions() {
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusDays(14);
        
        List<Shop> expiredPromotedShops = shopRepository.findByPromotedStatusAndLastPromotedAtBefore(
            PromotedStatus.PROMOTED, twoWeeksAgo
        );
        
        if (!expiredPromotedShops.isEmpty()) {
            log.info("Found {} shops with expired promotions", expiredPromotedShops.size());
            
            for (Shop shop : expiredPromotedShops) {
                shop.setPromotedStatus(PromotedStatus.NOT_PROMOTED);
                shop.setLastPromotedAt(null);
                log.debug("Expired promotion for shop: {} (ID: {})", shop.getName(), shop.getId());
            }
            
            shopRepository.saveAll(expiredPromotedShops);
            log.info("Successfully expired {} shop promotions", expiredPromotedShops.size());
        } else {
            log.debug("No expired shop promotions found");
        }
    }

    /**
     * Expire product promotions that are older than 1 day
     */
    private void expireProductPromotions() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        List<Product> expiredPromotedProducts = productRepository.findByPromotedStatusAndLastPromotedAtBefore(
            true, oneDayAgo
        );
        
        if (!expiredPromotedProducts.isEmpty()) {
            log.info("Found {} products with expired promotions", expiredPromotedProducts.size());
            
            for (Product product : expiredPromotedProducts) {
                product.setPromoted(false);
                product.setPromotedAt(null);
                
                // Decrement the shop's promoted product counter
                Shop shop = product.getShop();
                if (shop != null && shop.getPromotedNumber() > 0) {
                    shop.setPromotedNumber(shop.getPromotedNumber() - 1);
                    shopRepository.save(shop);
                    log.debug("Decremented promoted counter for shop: {} (ID: {})", shop.getName(), shop.getId());
                }
                
                log.debug("Expired promotion for product: {} (ID: {})", product.getName(), product.getId());
            }
            
            productRepository.saveAll(expiredPromotedProducts);
            log.info("Successfully expired {} product promotions", expiredPromotedProducts.size());
        } else {
            log.debug("No expired product promotions found");
        }
    }

    /**
     * Expire product features that are older than 1 day
     */
    private void expireProductFeatures() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        List<Product> expiredFeaturedProducts = productRepository.findByFeaturedStatusAndFeaturedAtBefore(
            FeaturedStatus.FEATURED, oneDayAgo
        );
        
        if (!expiredFeaturedProducts.isEmpty()) {
            log.info("Found {} products with expired features", expiredFeaturedProducts.size());
            
            for (Product product : expiredFeaturedProducts) {
                product.setFeaturedStatus(FeaturedStatus.NOT_FEATURED);
                product.setFeaturedAt(null);
                log.debug("Expired feature for product: {} (ID: {})", product.getName(), product.getId());
            }
            
            productRepository.saveAll(expiredFeaturedProducts);
            log.info("Successfully expired {} product features", expiredFeaturedProducts.size());
        } else {
            log.debug("No expired product features found");
        }
    }

    /**
     * Expire product floats that are older than 1 day
     */
    private void expireProductFloats() {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        
        List<Product> expiredFloatedProducts = productRepository.findByFloatedStatusAndLastPromotedAtBefore(
            true, oneDayAgo
        );
        
        if (!expiredFloatedProducts.isEmpty()) {
            log.info("Found {} products with expired floats", expiredFloatedProducts.size());
            
            for (Product product : expiredFloatedProducts) {
                product.setFloated(false);
                product.setFloatedAt(null);
                log.debug("Expired float for product: {} (ID: {})", product.getName(), product.getId());
            }
            
            productRepository.saveAll(expiredFloatedProducts);
            log.info("Successfully expired {} product floats", expiredFloatedProducts.size());
        } else {
            log.debug("No expired product floats found");
        }
    }

    /**
     * Manual method to trigger promotion expiry check (for testing or admin use)
     */
    public void manualExpirePromotions() {
        log.info("Manual promotion expiry check triggered");
        expirePromotions();
    }

    /**
     * Get statistics about current promotions
     */
    public PromotionStats getPromotionStats() {
        long promotedShops = shopRepository.findPromotedShops(PromotedStatus.PROMOTED).size();
        long featuredProducts = productRepository.findAllByFeatured(FeaturedStatus.FEATURED).size();
        long promotedProducts = productRepository.findAllByPromoted(true).size();
        long floatedProducts = productRepository.findAllByFloated(true).size();
        
        return PromotionStats.builder()
            .promotedShops(promotedShops)
            .featuredProducts(featuredProducts)
            .promotedProducts(promotedProducts)
            .floatedProducts(floatedProducts)
            .lastChecked(LocalDateTime.now())
            .build();
    }

    /**
     * DTO for promotion statistics
     */
    @lombok.Data
    @lombok.Builder
    public static class PromotionStats {
        private long promotedShops;
        private long featuredProducts;
        private long promotedProducts;
        private long floatedProducts;
        private LocalDateTime lastChecked;
    }
}