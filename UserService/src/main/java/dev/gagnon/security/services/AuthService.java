package dev.gagnon.security.services;

public interface AuthService {
    void blacklist(String token);
    boolean isTokenBlacklisted(String token);
}
