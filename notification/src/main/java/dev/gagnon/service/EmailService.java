package dev.gagnon.service;


import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;

public interface EmailService {
    void sendWelcomeEmail(String to, String name) throws MessagingException;
    void sendVerificationEmail(String toEmail, String firstName, String verificationToken, String frontendUrl) throws MessagingException, UnsupportedEncodingException;
}
