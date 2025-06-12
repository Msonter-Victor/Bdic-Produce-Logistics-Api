package dev.gagnon.Service;


import jakarta.mail.MessagingException;

public interface EmailService {
    void sendWelcomeEmail(String to, String name) throws MessagingException;
}
