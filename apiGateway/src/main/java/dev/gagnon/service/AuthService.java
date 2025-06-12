package dev.gagnon.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j
public class AuthService {
    private final WebClient.Builder webClient;

    public AuthService(WebClient.Builder webClient) {
        this.webClient = webClient;
    }


    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(
                webClient.build()
                        .get()
                        .uri("lb://user-service/is-token-blacklisted")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                        .doOnError(error -> log.error("Failed to check token blacklist status: {} - Error: {}", token, error.getMessage()))
                        .onErrorReturn(true)
                        .block()
        );
    }
}
