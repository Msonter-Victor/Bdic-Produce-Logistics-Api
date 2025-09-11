package dev.gagnon.service;

import com.pgvector.PGvector;
import dev.gagnon.dto.ChatResponse;
import dev.gagnon.model.ProductEmbedding;
import dev.gagnon.repository.ProductEmbeddingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SemanticProductSearchService {
    
    private final ProductEmbeddingRepository productEmbeddingRepository;
    private final EmbeddingService embeddingService;
    private final WebClient.Builder webClientBuilder;
    
    @Value("${services.vendor-service:http://localhost:8083}")
    private String vendorServiceUrl;
    
    public List<ProductEmbedding> searchSimilarProducts(String query, int limit) {
        try {
            PGvector queryEmbedding = embeddingService.generateEmbedding(query);
            String embeddingString = embeddingService.embeddingToString(queryEmbedding);
            return productEmbeddingRepository.findSimilarProducts(embeddingString, limit);
        } catch (Exception e) {
            log.error("Error in semantic product search for query: {}", query, e);
            return List.of();
        }
    }
    
    public List<ProductEmbedding> searchSimilarProductsInMarket(String query, String market, int limit) {
        try {
            PGvector queryEmbedding = embeddingService.generateEmbedding(query);
            String embeddingString = embeddingService.embeddingToString(queryEmbedding);
            return productEmbeddingRepository.findSimilarProductsInMarket(embeddingString, market, limit);
        } catch (Exception e) {
            log.error("Error in semantic product search for query: {} in market: {}", query, market, e);
            return List.of();
        }
    }
    
    public List<ChatResponse.ShopResult> findShopsWithSimilarProducts(String query, String market, int limit) {
        try {
            PGvector queryEmbedding = embeddingService.generateEmbedding(query);
            String embeddingString = embeddingService.embeddingToString(queryEmbedding);
            
            List<Object[]> results = productEmbeddingRepository.findShopsWithSimilarProducts(
                embeddingString, market, limit);
            
            return results.stream().map(row -> {
                ChatResponse.ShopResult shop = new ChatResponse.ShopResult();
                shop.setShopName((String) row[0]);
                shop.setShopId(((Number) row[1]).longValue());
                shop.setMarket((String) row[2]);
                shop.setAddress("Market Address"); // You might want to fetch this from shop service
                shop.setMarketSection("Section"); // You might want to fetch this from shop service
                shop.setProductCount(5); // You might want to calculate this
                return shop;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error finding shops with similar products for query: {} in market: {}", query, market, e);
            return List.of();
        }
    }
    
    public List<String> getAvailableMarkets() {
        return productEmbeddingRepository.findAllMarkets();
    }
    
    public void indexProduct(Long productId, String productName, String description, 
                           String category, String subCategory, String market, 
                           String shopName, Long shopId) {
        try {
            // Create searchable text combining all product information
            String searchableText = String.format("%s %s %s %s %s", 
                productName, description, category, subCategory, market);
            
            PGvector embedding = embeddingService.generateEmbedding(searchableText);
            
            ProductEmbedding productEmbedding = productEmbeddingRepository.findByProductId(productId)
                .orElse(new ProductEmbedding());
            
            productEmbedding.setProductId(productId);
            productEmbedding.setProductName(productName);
            productEmbedding.setDescription(description);
            productEmbedding.setCategory(category);
            productEmbedding.setSubCategory(subCategory);
            productEmbedding.setMarket(market);
            productEmbedding.setShopName(shopName);
            productEmbedding.setShopId(shopId);
            productEmbedding.setEmbedding(embedding);
            
            productEmbeddingRepository.save(productEmbedding);
            log.debug("Indexed product: {} (ID: {})", productName, productId);
        } catch (Exception e) {
            log.error("Error indexing product: {} (ID: {})", productName, productId, e);
        }
    }
    
    public void reindexAllProducts() {
        try {
            // Fetch all products from vendor service
            WebClient webClient = webClientBuilder.baseUrl(vendorServiceUrl).build();
            List<Map<String, Object>> products = webClient.get()
                .uri("/products/all")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {})
                .block();
            
            if (products != null) {
                for (Map<String, Object> product : products) {
                    indexProduct(
                        ((Number) product.get("id")).longValue(),
                        (String) product.get("name"),
                        (String) product.get("description"),
                        (String) product.get("category"),
                        (String) product.get("subCategory"),
                        (String) product.get("market"),
                        (String) product.get("shopName"),
                        ((Number) product.get("shopId")).longValue()
                    );
                }
                log.info("Reindexed {} products", products.size());
            }
        } catch (Exception e) {
            log.error("Error reindexing products", e);
        }
    }

    public List<Map<String, Object>> searchProducts(String userQuery) {
        return null;
    }
}