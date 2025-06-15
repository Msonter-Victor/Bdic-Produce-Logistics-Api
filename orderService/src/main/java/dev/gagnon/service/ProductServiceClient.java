package dev.gagnon.service;

import dev.gagnon.dto.response.ProductResponseDto;
import dev.gagnon.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    public ProductResponseDto getProduct(Long productId) {
        try {
            ProductResponseDto response = restTemplate.getForObject(
                    "http://vendor-service/api/products/{id}",
                    ProductResponseDto.class,
                    productId
            );
            log.info("Product:{}",response);
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            throw new BusinessException("Product not found with ID: " + productId);
        } catch (RestClientException e) {
            throw new BusinessException("Error communicating with vendor service: " + e.getMessage());
        }
    }

    public boolean checkProductAvailability(Long productId, Integer quantity) {
        try {
            return Boolean.TRUE.equals(restTemplate.getForObject(
                    "http://vendor-service/api/products/{id}/availability?quantity={quantity}",
                    Boolean.class,
                    productId,
                    quantity
            ));
        } catch (RestClientException e) {
            throw new BusinessException("Error checking product availability: " + e.getMessage());
        }
    }

    public void updateProductStock(Long productId, Integer quantity) {
        try {
            Map<String, Integer> request = Map.of("quantity", quantity);
            restTemplate.put(
                    "http://vendor-service/api/products/{id}/stock",
                    request,
                    productId
            );
        } catch (RestClientException e) {
            throw new BusinessException("Failed to update product stock: " + productId);
        }
    }
}