package com.tripflow.kafka;

import com.tripflow.kafka.event.TripCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TripEventProducer {

    private static final String TOPIC = "trip-events";

    private final KafkaTemplate<String, TripCreatedEvent> kafkaTemplate;

    public TripEventProducer(
            KafkaTemplate<String, TripCreatedEvent> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTripCreatedEvent(TripCreatedEvent event) {
        kafkaTemplate.send(TOPIC, event.tripId().toString(), event);
    }
}