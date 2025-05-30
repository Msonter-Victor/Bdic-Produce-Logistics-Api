package dev.gagnon.service;

import dev.gagnon.dto.response.ProductResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class ProductServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${services.vendor-service.url}")
    private String vendorServiceUrl;
    
    public ProductResponse getProduct(Long productId) {
        try {
            String url = vendorServiceUrl + "/api/products/" + productId;
            return restTemplate.getForObject(url, ProductResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch product: " + productId, e);
        }
    }
    
    public boolean checkProductAvailability(Long productId, Integer quantity) {
        try {
            String url = vendorServiceUrl + "/products/" + productId + "/availability?quantity=" + quantity;
            Boolean available = restTemplate.getForObject(url, Boolean.class);
            return available == null || !available;
        } catch (Exception e) {
            return true;
        }
    }
    
    public void updateProductStock(Long productId, Integer quantity) {
        try {
            String url = vendorServiceUrl + "/products/" + productId + "/stock";
            Map<String, Integer> request = Map.of("quantity", quantity);
            restTemplate.put(url, request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update product stock: " + productId, e);
        }
    }
}