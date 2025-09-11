package dev.gagnon.service;

import dev.gagnon.config.MailConfig;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private final MailConfig mailConfig;

    public void sendWelcomeEmail(String to, String name) throws MessagingException {
        String subject = "Registration Successful!";
        String body = buildWelcomeEmailBody(name);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(mailConfig.getSenderEmail());
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }

    public void sendVerificationEmail(String toEmail, String firstName, String verificationToken, String frontendUrl) throws MessagingException, UnsupportedEncodingException {
            System.out.println("Attempting to send verification email to: " + toEmail);
            System.out.println("Frontend URL: " + frontendUrl);
            System.out.println("Verification Token: " + verificationToken);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailConfig.getSenderEmail(), "BDIC Virtual Market");
            helper.setTo(toEmail);
            helper.setSubject("Verify your email address");
            String verificationUrl = frontendUrl + "?token=" + verificationToken;
            System.out.println("Complete verification URL: " + verificationUrl);
            
            String htmlContent = buildVerificationEmailTemplate(firstName, verificationUrl);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("Verification email sent successfully to: " + toEmail);
    }

    private String buildVerificationEmailTemplate(String firstName, String verificationUrl) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head><meta charset='UTF-8'><title>Email Verification</title></head>" +
                "<body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>" +
                "<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>" +
                "<h2 style='color: #2c3e50;'>Welcome to " + "BDIC Virtual and Digital Market and Logistics Management System" + "!</h2>" +
                "<p>Hi " + firstName + ",</p>" +
                "<p>Thank you for registering with us. To complete your registration and activate your account, please click the button below to verify your email address:</p>" +
                "<div style='text-align: center; margin: 30px 0;'>" +
                "<a href='" + verificationUrl + "' style='background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Verify Email Address</a>" +
                "</div>" +
                "<p>If the button doesn't work, you can copy and paste the following link into your browser:</p>" +
                "<p style='background-color: #f8f9fa; padding: 10px; border-radius: 5px; word-break: break-all; font-size: 14px;'>" + verificationUrl + "</p>" +
                "<p style='color: #7f8c8d; font-size: 14px;'><strong>Note:</strong> This verification link will expire in 24 hours.</p>" +
                "<p>If you didn't create an account with us, please ignore this email.</p>" +
                "<p>Best regards,<br>The " + "BDIC" + " Team</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildWelcomeEmailBody(String name) {
        return "<html>" +
                "<body>" +
                "<h2>Hello, " + name + "!</h2>" +
                "<p>We are excited to have you onboard. Get ready to explore amazing features!</p>" +
                "<p>If you have any questions, feel free to reach out.</p>" +
                "<br>" +
                "<p>Best Regards,</p>" +
                "<p><b>Produce and Logistics Support Team</b></p>" +
                "</body>" +
                "</html>";
    }
}
