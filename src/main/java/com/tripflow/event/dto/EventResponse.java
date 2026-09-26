package com.tripflow.event.dto;

import java.time.LocalDateTime;

public record EventResponse(
        Long id,
        String title,
        String description,
        LocalDateTime startTime,
        Long tripId
) {
}