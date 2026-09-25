package com.tripflow.trip.dto;

public record TripResponse(
        Long id,
        String title,
        String description
) {
}