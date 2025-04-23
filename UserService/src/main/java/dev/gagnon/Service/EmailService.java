package dev.gagnon.Service;

public interface EmailService {
    void sendVerificationEmail(String to, String subject, String body);
}
