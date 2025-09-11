package dev.gagnon.service;

import dev.gagnon.dto.request.NotificationEvent;
import dev.gagnon.dto.response.DeliveryOrderResponse;
import dev.gagnon.dto.response.ProductResponse;
import dev.gagnon.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@AllArgsConstructor
public class ServiceClient {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(ServiceClient.class);
    private final RestTemplate restTemplate;

    public ProductResponse getProduct(Long productId) {
        try {
            ProductResponse response = this.restTemplate.getForObject("http://vendor-service/api/products/{id}", ProductResponse.class, new Object[]{productId});
            log.info("Product:{}", response);
            return response;
        }
        catch (HttpClientErrorException.NotFound e) {
            throw new BusinessException("Product not found with ID: " + productId);
        }
        catch (RestClientException e) {
            throw new BusinessException("Error communicating with vendor service: " + e.getMessage());
        }
    }

    public boolean checkProductAvailability(Long productId, Integer quantity) {
        try {
            return Boolean.TRUE.equals(this.restTemplate.getForObject("http://vendor-service/api/products/{id}/availability?quantity={quantity}", Boolean.class, new Object[]{productId, quantity}));
        }
        catch (RestClientException e) {
            throw new BusinessException("Error checking product availability: " + e.getMessage());
        }
    }

    public void updateProductStock(Long productId, Integer quantity) {
        try {
            Map<String, Integer> request = Map.of("quantity", quantity);
            this.restTemplate.put("http://vendor-service/api/products/{id}/stock", request, productId);
        }
        catch (RestClientException e) {
            throw new BusinessException("Failed to update product stock: " + productId);
        }
    }

    public void sendNotification(NotificationEvent event) {
        try {
            this.restTemplate.postForObject("http://notification-service/api/notification/send", (Object)event, Void.class, new Object[0]);
        }
        catch (RestClientException e) {
            throw new BusinessException("Failed to send order notification: " + e.getMessage());
        }
    }

    public DeliveryOrderResponse createOrder(Long orderId) {
        try {
            DeliveryOrderResponse response = this.restTemplate.postForObject(
                    "http://logistics-service/api/logistics/create-order?orderId={orderId}",
                    null,
                    DeliveryOrderResponse.class,
                    orderId
            );
            log.info("Delivery Order created: {}", response);
            return response;
        } catch (HttpClientErrorException.NotFound e) {
            throw new BusinessException("Order not found with ID: " + orderId);
        } catch (RestClientException e) {
            throw new BusinessException("Error communicating with logistics service: " + e.getMessage());
        }

    }
}

