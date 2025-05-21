package dev.gagnon.utils;

import dev.gagnon.dto.request.InitializePaymentRequest;
import dev.gagnon.dto.response.InitializePaymentResponse;
import dev.gagnon.dto.response.VerifyPaymentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CredoApiClient {
    @Value("${credo.api.url}")
    private String credoApiBaseUrl;

    @Value("${credo.api.public.key}")
    private String publicKey;

    @Value("${credo.api.private.key}")
    private String secretKey;

    private final RestTemplate restTemplate;

    public CredoApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public InitializePaymentResponse initializePayment(InitializePaymentRequest request) {
        String url = credoApiBaseUrl + "/transaction/initialize";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", publicKey);

        // Generate a unique reference if not provided
        String reference = generateUniqueReference();
        String[] channels = {"bank", "card"};

        Map<String, Object> payload = new HashMap<>();
        payload.put("email", request.getEmail());
        payload.put("amount", request.getAmount());
        payload.put("reference", reference); // Always include reference
        payload.put("currency", request.getCurrency());
        payload.put("callbackUrl", request.getCallbackUrl());
        payload.put("channels", channels);


        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<InitializePaymentResponse> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                InitializePaymentResponse.class
        );

        return response.getBody();
    }

    private String generateUniqueReference() {
        // Generate a unique reference using timestamp and UUID
        return "CREDO-" + System.currentTimeMillis() + "-" +
                UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    public VerifyPaymentResponse verifyPayment(String transRef) {
        String url = credoApiBaseUrl + "/transaction/" + transRef + "/verify";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", secretKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<VerifyPaymentResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                VerifyPaymentResponse.class
        );

        return response.getBody();
    }
}