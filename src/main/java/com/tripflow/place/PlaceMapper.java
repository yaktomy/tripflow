package com.tripflow.place;

import com.tripflow.place.dto.PlaceResponse;
import org.springframework.stereotype.Component;

@Component
public class PlaceMapper {

    public PlaceResponse toResponse(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getName(),
                place.getAddress(),
                place.getDescription(),
                place.getTrip().getId()
        );
    }
}