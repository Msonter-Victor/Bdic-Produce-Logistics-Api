package dev.gagnon.service.impls;

import dev.gagnon.dto.response.UserCreateResponse;
import jakarta.annotation.PostConstruct;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Service;

@Service
public class EventPublisher {
    private final Producer<UserCreateResponse> producer;

    public EventPublisher(PulsarClient pulsarClient) throws PulsarClientException {
        this.producer = pulsarClient.newProducer(Schema.JSON(UserCreateResponse.class))
                .topic("create-user-pub")
                .create();
    }



    public void sendCreateMessage(UserCreateResponse response) {
        try {
            producer.send(response);
            producer.close();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }
}
