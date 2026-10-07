package com.tripflow.kafka;

import com.tripflow.kafka.event.TripCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TripEventConsumer {

    @KafkaListener(
            topics = "trip-events",
            groupId = "tripflow-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeTripCreatedEvent(TripCreatedEvent event) {
        System.out.println(
                "Received TripCreatedEvent: " +
                        "tripId=" + event.tripId() +
                        ", ownerId=" + event.ownerId() +
                        ", title=" + event.title()
        );
    }
}