package dev.gagnon.service;

import dev.gagnon.dto.response.CreateUserEvent;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventListener {

    private final PulsarClient pulsarClient;
    private final EmailService emailService;

    public EventListener(PulsarClient pulsarClient, EmailService emailService) throws PulsarClientException {
        this.pulsarClient = pulsarClient;
        this.emailService = emailService;
        startEventListeners();
    }

    private void startEventListeners() throws PulsarClientException {
        handleCreatedUserEvent();

    }

    private void handleCreatedUserEvent() throws PulsarClientException {
        Consumer<CreateUserEvent> consumer = pulsarClient.newConsumer(Schema.JSON(CreateUserEvent.class))
                .topic("create-user-pub")
                .subscriptionName("create-user-sub")
                .subscriptionType(SubscriptionType.Shared)  // Optional: Set subscription type explicitly
                .messageListener(this::processCreateUserEvent)
                .subscribe();

        log.info("Consumer for CreateUserEvent subscribed successfully.");
    }

    private void processCreateUserEvent(Consumer<CreateUserEvent> consumer, Message<CreateUserEvent> msg) {
        try {
            CreateUserEvent createUserEvent = msg.getValue();
            emailService.sendWelcomeEmail(createUserEvent.getEmail(), createUserEvent.getFirstName());
            log.info("CreateUserEvent received: {}", createUserEvent);
            consumer.acknowledge(msg);
        } catch (PulsarClientException | MessagingException e) {
            log.error("Error processing CreateUserEvent: {}", e.getMessage(), e);
            consumer.negativeAcknowledge(msg);
        }
    }
}
