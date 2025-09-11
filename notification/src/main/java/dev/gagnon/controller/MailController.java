package dev.gagnon.controller;

import dev.gagnon.dto.request.SendMailRequest;
import dev.gagnon.dto.request.VerificationRequest;
import dev.gagnon.service.EmailService;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/v1/mail")
public class MailController {
    private final EmailService emailService;

    public MailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> sendMail(@RequestBody SendMailRequest request) throws MessagingException {
        try{
            emailService.sendWelcomeEmail(request.getTo(),request.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
        }
    }
    @PostMapping("/send-verification-mail")
    public ResponseEntity<?>sendVerificationMail(@RequestBody VerificationRequest request) throws MessagingException, UnsupportedEncodingException {
        emailService.sendVerificationEmail
                (request.getToEmail(),request.getFirstName(),request.getVerificationToken(),request.getFrontendUrl());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
