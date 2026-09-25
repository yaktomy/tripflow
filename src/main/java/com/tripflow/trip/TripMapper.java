package com.tripflow.trip;

import com.tripflow.trip.dto.TripResponse;
import org.springframework.stereotype.Component;

@Component
public class TripMapper {

    public TripResponse toResponse(Trip trip) {
        return new TripResponse(
                trip.getId(),
                trip.getTitle(),
                trip.getDescription()
        );
    }
}