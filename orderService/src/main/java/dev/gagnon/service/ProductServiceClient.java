package dev.gagnon.service;

import dev.gagnon.dto.response.ProductResponseDto;
import dev.gagnon.exception.BusinessException;
import dev.gagnon.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceClient {
    
    private final RestTemplate restTemplate;

    
    public ProductResponseDto getProduct(Long productId) {
        try {

            ResponseEntity<ProductResponseDto> response = restTemplate.getForEntity(
                "https://api.digitalmarke.bdic.ng/api/products/{id}",
                ProductResponseDto.class,
                    productId
            );
            return response.getBody();
        } catch (Exception e) {
            throw new ResourceNotFoundException("Product not found: " + productId);
        }
    }
    
    public boolean checkProductAvailability(Long productId, Integer quantity) {
        try {
            String url = "http://vendor-service/api/products/{id}" + "/availability?quantity=" + quantity;
            ResponseEntity<Boolean> response = restTemplate.getForEntity(
                url, 
                Boolean.class,
                    productId
            );
            return Boolean.TRUE.equals(response.getBody());
        } catch (Exception e) {
            return false;
        }
    }
    
    public void updateProductStock(Long productId, Integer quantity) {
        try {
            String url = "http://api/products/" + productId + "/stock";
            Map<String, Integer> request = Map.of("quantity", quantity);
            restTemplate.put(url, request);
        } catch (Exception e) {
            throw new BusinessException("Failed to update product stock: " + productId);
        }
    }
}