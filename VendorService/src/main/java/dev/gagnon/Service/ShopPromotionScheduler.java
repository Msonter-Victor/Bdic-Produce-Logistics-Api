package dev.gagnon.Service;

import dev.gagnon.Exception.ResourceNotFoundException;
import dev.gagnon.Model.Product;
import dev.gagnon.Model.Shop;
import dev.gagnon.Model.ShopTier;
import dev.gagnon.Model.constants.PromotedStatus;
import dev.gagnon.Model.constants.PromotionType;
import dev.gagnon.Repository.ProductRepository;
import dev.gagnon.Repository.ShopRepository;
import dev.gagnon.Repository.TierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopPromotionScheduler {

    private final ShopRepository shopRepository;
    private final TierRepository tierRepository;
    private final ProductRepository productRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Every day at 00:00
    public void demoteExpiredPromotions() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Shop> expiredShops = shopRepository.findByPromotedStatusAndLastPromotedAtBefore(
                PromotedStatus.PROMOTED, thirtyDaysAgo
        );

        for (Shop shop : expiredShops) {
            ShopTier tier = tierRepository.findById(shop.getShopTier().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Shop Tier not found"));
            tier.setShopsPromoted(tier.getShopsPromoted() - 1);
            tierRepository.save(tier);
            shop.setPromotedStatus(PromotedStatus.NOT_PROMOTED);
            shop.setShopTier(null);
            shop.setLastPromotedAt(null);
            shop.setFeaturedNumber(0L);
            shop.setFloatedNumber(0L);
            shop.setPromotedNumber(0L);
        }

        shopRepository.saveAll(expiredShops);
        log.info("Demoted {} shops with expired promotions", expiredShops.size());
    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at 00:00
    public void demoteExpiredProductPromotions() {
        LocalDateTime days = LocalDateTime.now().minusDays(7);
        List<Product> expiredProducts = productRepository.findByPromotedStatusAndLastPromotedAtBefore(
                true, days
        );
        List<Product> expiredFloatedProducts = productRepository.findByFloatedStatusAndLastPromotedAtBefore(
                true, days
        );

        for (Product product : expiredProducts) {
            product.setPromoted(false);
            product.setPromotedAt(null);
            Shop shop = product.getShop();
            shop.setPromotedNumber(shop.getPromotedNumber() - 1);
            shopRepository.save(shop);
            productRepository.save(product);
        }
        for (Product product : expiredFloatedProducts) {
            product.setFloated(false);
            product.setFloatedAt(null);
            Shop shop = product.getShop();
            shop.setPromotedNumber(shop.getFloatedNumber() - 1);
            shopRepository.save(shop);
            productRepository.save(product);
        }

        productRepository.saveAll(expiredProducts);
        log.info("Demoted products with expired promotions");
    }

}
