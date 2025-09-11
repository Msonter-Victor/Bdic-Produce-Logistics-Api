//CredoApiClient.java
package dev.gagnon.utils;

import dev.gagnon.dto.request.InitializePaymentRequest;
import dev.gagnon.dto.response.InitializePaymentResponse;
import dev.gagnon.dto.response.VerifyPaymentResponse;
import dev.gagnon.execption.PaymentException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CredoApiClient {
    private final RestTemplate externalApiRestTemplate; // Injected by name

    @Value("${credo.api.url}")
    private String credoApiBaseUrl;

    @Value("${credo.api.public.key}")
    private String publicKey;

    @Value("${credo.api.private.key}")
    private String secretKey;

    public InitializePaymentResponse initializePayment(InitializePaymentRequest request) {
        String url = credoApiBaseUrl + "/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", publicKey);

        String reference = generateUniqueReference();
        String[] channels = {"bank", "card"};

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.getEmail());
        payload.put("amount", request.getAmount());
        payload.put("reference", reference);
        payload.put("currency", request.getCurrency());
        payload.put("callbackUrl", request.getCallbackUrl());
        payload.put("channels", channels);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<InitializePaymentResponse> response = externalApiRestTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    InitializePaymentResponse.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new PaymentException("Failed to initialize payment with Credo: " + e.getMessage());
        }
    }

    private String generateUniqueReference() {
        return "CREDO-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public VerifyPaymentResponse verifyPayment(String transRef) {
        String url = credoApiBaseUrl + "/transaction/" + transRef + "/verify";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", secretKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<VerifyPaymentResponse> response = externalApiRestTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    VerifyPaymentResponse.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            throw new PaymentException("Failed to verify payment with Credo: " + e.getMessage());
        }
    }
}