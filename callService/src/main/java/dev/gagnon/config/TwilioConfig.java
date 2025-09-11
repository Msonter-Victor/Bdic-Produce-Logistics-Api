package dev.gagnon.config;

import com.twilio.Twilio;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
@Slf4j
public class TwilioConfig {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.api-key}")
    private String apiKey;

    @Value("${twilio.api-secret}")
    private String apiSecret;

    @PostConstruct
    public void initTwilio() {
        try {
            Twilio.init(apiKey, apiSecret, accountSid);
            log.info("Twilio initialized successfully with API Key");
        } catch (Exception e) {
            log.error("Failed to initialize Twilio: {}", e.getMessage());
            throw new RuntimeException("Twilio initialization failed", e);
        }
    }

}