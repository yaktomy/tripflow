package com.tripflow.kafka.event;

public record TripCreatedEvent(
        Long tripId,
        Long ownerId,
        String title
) {
}