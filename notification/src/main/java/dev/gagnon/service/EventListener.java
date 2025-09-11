package dev.gagnon.service;

import dev.gagnon.dto.request.EmailVerificationEvent;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class EventListener {

//    private final PulsarClient pulsarClient;
//    private final EmailService emailService;
//
//    public EventListener(PulsarClient pulsarClient, EmailService emailService) throws PulsarClientException {
//        this.pulsarClient = pulsarClient;
//        this.emailService = emailService;
//        System.out.println("EventListener initializing...");
//        startEventListeners();
//        System.out.println("EventListener initialized successfully");
//    }
//
//    private void startEventListeners() throws PulsarClientException {
//        handleCreatedUserEvent();
//
//    }
//
//    private void handleCreatedUserEvent() throws PulsarClientException {
//        System.out.println("Setting up consumer for topic: verify-email-pub");
//
//        Consumer<EmailVerificationEvent> consumer = pulsarClient.newConsumer(Schema.JSON(EmailVerificationEvent.class))
//                .topic("verify-email-pub")
//                .subscriptionName("verify-email-sub")
//                .subscriptionType(SubscriptionType.Shared)  // Optional: Set subscription type explicitly
//                .messageListener(this::processCreateUserEvent)
//                .subscribe();
//
//        log.info("Consumer for CreateUserEvent subscribed successfully to topic: verify-email-pub");
//        System.out.println("Consumer subscribed successfully to topic: verify-email-pub with subscription: verify-email-sub");
//    }
//
//    private void processCreateUserEvent(Consumer<EmailVerificationEvent> consumer, Message<EmailVerificationEvent> msg) {
//        try {
//            EmailVerificationEvent createUserEvent = msg.getValue();
//            log.info("CreateUserEvent received: {}", createUserEvent);
//            System.out.println("Processing email verification event for: " + createUserEvent.getEmail());
//
//            emailService.sendVerificationEmail(createUserEvent.getEmail(), createUserEvent.getFirstName(), createUserEvent.getVerificationToken(), createUserEvent.getFrontendUrl());
//            consumer.acknowledge(msg);
//            System.out.println("Email verification event processed successfully for: " + createUserEvent.getEmail());
//        } catch (PulsarClientException | MessagingException e) {
//            log.error("Error processing CreateUserEvent: {}", e.getMessage(), e);
//            System.err.println("Error processing email verification event: " + e.getMessage());
//            consumer.negativeAcknowledge(msg);
//        } catch (UnsupportedEncodingException e) {
//            log.error("Encoding error processing CreateUserEvent: {}", e.getMessage(), e);
//            System.err.println("Encoding error processing email verification event: " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }
}
