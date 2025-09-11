package dev.gagnon.service;

import dev.gagnon.model.ProductEmbedding;
import dev.gagnon.repository.ProductEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataSynchronizationService {
    
    private final ProductEmbeddingRepository productEmbeddingRepository;
    private final EmbeddingService embeddingService;
    private final ServiceClient serviceClient;
    
    /**
     * Synchronize products from the main database every hour
     * DISABLED: Requires OpenAI API quota
     */
    // @Scheduled(fixedRate = 3600000) // 1 hour
    public void synchronizeProducts() {
        log.info("Product synchronization is disabled (requires OpenAI API quota)");
        // Commented out to avoid OpenAI API calls
        /*
        try {
            log.info("Starting product synchronization...");
            
            List<Map<String, Object>> products = serviceClient.getAllProducts();
            int syncedCount = 0;
            
            for (Map<String, Object> productData : products) {
                try {
                    syncProduct(productData);
                    syncedCount++;
                } catch (Exception e) {
                    log.error("Error syncing product: " + productData.get("id"), e);
                }
            }
            
            log.info("Product synchronization completed. Synced {} products", syncedCount);
        } catch (Exception e) {
            log.error("Error during product synchronization", e);
        }
        */
    }
    
    /**
     * Manual synchronization method that can be called on startup or via API
     * DISABLED: Requires OpenAI API quota
     */
    public void performFullSync() {
        log.info("Full product synchronization is disabled (requires OpenAI API quota)");
        // synchronizeProducts(); // Commented out to avoid OpenAI API calls
    }
    
    private void syncProduct(Map<String, Object> productData) {
        Long productId = ((Number) productData.get("id")).longValue();
        
        // Check if product already exists
        ProductEmbedding existingProduct = productEmbeddingRepository.findByProductId(productId).orElse(null);
        
        String productName = (String) productData.get("name");
        String description = (String) productData.get("description");
        String category = (String) productData.get("category");
        String subCategory = (String) productData.get("subCategory");
        String market = (String) productData.get("market");
        String shopName = (String) productData.get("shopName");
        Long shopId = productData.get("shopId") != null ? ((Number) productData.get("shopId")).longValue() : null;
        
        // Create text for embedding
        String textForEmbedding = createEmbeddingText(productName, description, category, subCategory);
        
        if (existingProduct != null) {
            // Update existing product if data has changed
            boolean hasChanged = !textForEmbedding.equals(getExistingEmbeddingText(existingProduct));
            
            if (hasChanged) {
                existingProduct.setProductName(productName);
                existingProduct.setDescription(description);
                existingProduct.setCategory(category);
                existingProduct.setSubCategory(subCategory);
                existingProduct.setMarket(market);
                existingProduct.setShopName(shopName);
                existingProduct.setShopId(shopId);
                existingProduct.setEmbedding(embeddingService.generateEmbedding(textForEmbedding));
                existingProduct.setUpdatedAt(LocalDateTime.now());
                
                productEmbeddingRepository.save(existingProduct);
                log.debug("Updated product embedding for: {}", productName);
            }
        } else {
            // Create new product embedding
            ProductEmbedding newProduct = new ProductEmbedding();
            newProduct.setProductId(productId);
            newProduct.setProductName(productName);
            newProduct.setDescription(description);
            newProduct.setCategory(category);
            newProduct.setSubCategory(subCategory);
            newProduct.setMarket(market);
            newProduct.setShopName(shopName);
            newProduct.setShopId(shopId);
            newProduct.setEmbedding(embeddingService.generateEmbedding(textForEmbedding));
            newProduct.setCreatedAt(LocalDateTime.now());
            newProduct.setUpdatedAt(LocalDateTime.now());
            
            productEmbeddingRepository.save(newProduct);
            log.debug("Created new product embedding for: {}", productName);
        }
    }
    
    private String createEmbeddingText(String name, String description, String category, String subCategory) {
        StringBuilder text = new StringBuilder();
        
        if (name != null) text.append(name).append(" ");
        if (description != null) text.append(description).append(" ");
        if (category != null) text.append(category).append(" ");
        if (subCategory != null) text.append(subCategory).append(" ");
        
        return text.toString().trim();
    }
    
    private String getExistingEmbeddingText(ProductEmbedding product) {
        return createEmbeddingText(
            product.getProductName(),
            product.getDescription(),
            product.getCategory(),
            product.getSubCategory()
        );
    }
    
    /**
     * Remove embeddings for products that no longer exist
     */
    public void cleanupOrphanedEmbeddings() {
        try {
            log.info("Starting cleanup of orphaned embeddings...");
            
            List<ProductEmbedding> allEmbeddings = productEmbeddingRepository.findAll();
            List<Map<String, Object>> currentProducts = serviceClient.getAllProducts();
            
            List<Long> currentProductIds = currentProducts.stream()
                .map(p -> ((Number) p.get("id")).longValue())
                .toList();
            
            int deletedCount = 0;
            for (ProductEmbedding embedding : allEmbeddings) {
                if (!currentProductIds.contains(embedding.getProductId())) {
                    productEmbeddingRepository.delete(embedding);
                    deletedCount++;
                    log.debug("Deleted orphaned embedding for product ID: {}", embedding.getProductId());
                }
            }
            
            log.info("Cleanup completed. Deleted {} orphaned embeddings", deletedCount);
        } catch (Exception e) {
            log.error("Error during cleanup of orphaned embeddings", e);
        }
    }
}