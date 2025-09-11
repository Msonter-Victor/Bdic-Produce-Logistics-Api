package dev.gagnon.service;

import dev.gagnon.dto.MarketResponseDto;
import dev.gagnon.dto.ProductResponseDto;
import dev.gagnon.dto.ShopResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceClient {

    private final RestTemplate restTemplate;

    /**
     * Search products using the existing search endpoint
     */
    public List<ProductResponseDto> searchProducts(String query) {
        try {
            ResponseEntity<List<ProductResponseDto>> response = restTemplate.exchange(
                    "http://VENDOR-SERVICE/api/products/search-product?keyword={query}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ProductResponseDto>>() {},
                    query
            );
            
            List<ProductResponseDto> products = response.getBody();
            log.debug("Found {} products matching query: {}", products != null ? products.size() : 0, query);
            return products != null ? products : Collections.emptyList();
            
        } catch (RestClientException e) {
            log.error("Error searching products with query '{}': {}", query, e.getMessage());
            return Collections.emptyList();
        }
    }


    public List<MarketResponseDto> getAllMarkets() {
        try {
            ResponseEntity<List<MarketResponseDto>> response = restTemplate.exchange(
                    "http://VENDOR-SERVICE/api/markets/all",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<MarketResponseDto>>() {}
            );
            
            List<MarketResponseDto> markets = response.getBody();
            log.debug("Fetched {} markets from vendor service", markets != null ? markets.size() : 0);
            return markets != null ? markets : Collections.emptyList();
            
        } catch (RestClientException e) {
            log.error("Error fetching markets from vendor service: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get shops by market ID
     */
    public List<ShopResponseDto> getShopsByMarket(Long marketId) {
        try {
            ResponseEntity<List<ShopResponseDto>> response = restTemplate.exchange(
                    "http://VENDOR-SERVICE/api/shops/by-market?marketId={marketId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ShopResponseDto>>() {},
                    marketId
            );
            
            List<ShopResponseDto> shops = response.getBody();
            log.debug("Found {} shops in market {}", shops != null ? shops.size() : 0, marketId);
            return shops != null ? shops : Collections.emptyList();
            
        } catch (RestClientException e) {
            log.error("Error fetching shops for market {}: {}", marketId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get markets that have products matching the search query
     */
    public List<MarketResponseDto> getMarketsWithProducts(String productQuery) {
        try {
            // First search for products
            List<ProductResponseDto> products = searchProducts(productQuery);
            
            if (products.isEmpty()) {
                return Collections.emptyList();
            }
            
            // Get all markets and filter those that have the searched products
            List<MarketResponseDto> allMarkets = getAllMarkets();
            
            return allMarkets.stream()
                .filter(market -> products.stream()
                    .anyMatch(product -> product.getMarketName() != null && 
                             product.getMarketName().equals(market.getName())))
                .toList();
            
        } catch (Exception e) {
            log.error("Error finding markets with products matching '{}': {}", productQuery, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Get all products (for backward compatibility with DataSynchronizationService)
     */
    public List<Map<String, Object>> getAllProducts() {
        try {
            List<ProductResponseDto> products = searchProducts(""); // Empty query to get all
            
            return products.stream().map(product -> {
                Map<String, Object> productMap = new HashMap<>();
                productMap.put("id", product.getId());
                productMap.put("name", product.getName());
                productMap.put("description", product.getDescription());
                productMap.put("price", product.getPrice());
                productMap.put("quantity", product.getQuantity());
                productMap.put("quantitySold", product.getQuantitySold());
                productMap.put("categoryName", product.getCategoryName());
                productMap.put("subCategoryName", product.getSubCategoryName());
                productMap.put("shopName", product.getShopName());
                productMap.put("shopId", product.getShopId());
                productMap.put("marketName", product.getMarketName());
                productMap.put("marketId", product.getMarketId());
                return productMap;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            log.error("Error fetching all products: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getUserInfo(String sessionId) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://USER-SERVICE/api/users/session/{sessionId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {},
                    sessionId
            );
            
            Map<String, Object> userInfo = response.getBody();
            log.debug("Retrieved user info for session: {}", sessionId);
            return userInfo != null ? userInfo : Collections.emptyMap();
            
        } catch (RestClientException e) {
            log.warn("Could not retrieve user info for session '{}': {}", sessionId, e.getMessage());
            return Collections.emptyMap();
        }
    }
}