package dev.gagnon.utils;

import dev.gagnon.dto.response.OrderCreatedDto;
import dev.gagnon.model.Order;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
import org.springframework.stereotype.Component;

@Component
public class EventPublisherAdapter  {
    private final Producer<OrderCreatedDto> producer;

    public EventPublisherAdapter(PulsarClient pulsarClient) throws PulsarClientException {
        this.producer = pulsarClient.newProducer(Schema.JSON(OrderCreatedDto.class))
                .topic("create-order-pub")
                .create();
    }



    public void sendCreateMessage(OrderCreatedDto order) {
        try {
            producer.send(order);
            producer.close();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

}