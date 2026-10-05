package com.tripflow.place.dto;

public record PlaceResponse(
        Long id,
        String name,
        String address,
        String description,
        Long tripId
) {
}