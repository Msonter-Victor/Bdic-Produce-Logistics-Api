package dev.gagnon.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AuthService {
    private WebClient webClient;

    public boolean isTokenBlacklisted(String token) {

    }
}
