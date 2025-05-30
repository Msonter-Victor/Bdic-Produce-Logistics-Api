package dev.gagnon.service;

import dev.gagnon.dto.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Value("${services.user-service.url}")
    private String userServiceUrl;
    
    public UserResponse getUser(Long userId) {
        try {
            String url = userServiceUrl + "/users/" + userId;
            return restTemplate.getForObject(url, UserResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user: " + userId, e);
        }
    }
}
