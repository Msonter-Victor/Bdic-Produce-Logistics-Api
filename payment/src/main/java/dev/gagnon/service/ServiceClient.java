//ServiceClient.java
package dev.gagnon.service;

import dev.gagnon.dto.response.UserResponse;
import dev.gagnon.execption.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceClient {

    private final RestTemplate loadBalancedRestTemplate; // Injected by name

    public UserResponse getUser(String email) {
        try {
            String url = "http://user-service/api/users/get-profile?email={email}";
            Map<String, String> params = Collections.singletonMap("email", email);

            UserResponse response = loadBalancedRestTemplate.getForObject(
                    url,
                    UserResponse.class,
                    params
            );

            log.info("User: {}", response);
            return response;

        } catch (HttpClientErrorException.NotFound e) {
            throw new BusinessException("User not found: " + email);
        } catch (RestClientException e) {
            throw new BusinessException("Error communicating with user service: " + e.getMessage());
        }
    }
}