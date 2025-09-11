package dev.gagnon.Service;

import dev.gagnon.DTO.request.VerificationRequest;
import dev.gagnon.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceClient {

    private final RestTemplate restTemplate;


    public void sendVerificationMail(VerificationRequest request) {
        try {
            restTemplate.postForObject(
                    "http://notification-service/api/v1/mail/send-verification-mail",
                    request,
                    Void.class
            );
        } catch (RestClientException e) {
            throw new BusinessException("Failed to send mail "+e.getMessage());
        }
    }

}