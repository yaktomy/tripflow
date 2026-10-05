package com.tripflow.place;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.trip.Trip;
import com.tripflow.trip.TripRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final TripRepository tripRepository;

    public PlaceService(
            PlaceRepository placeRepository,
            TripRepository tripRepository
    ) {
        this.placeRepository = placeRepository;
        this.tripRepository = tripRepository;
    }

    public Place createPlace(
            Long tripId,
            String name,
            String address,
            String description
    ) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip with id " + tripId + " not found"
                ));

        Place place = new Place(name, address, description, trip);

        return placeRepository.save(place);
    }

    public List<Place> getPlaces(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException(
                    "Trip with id " + tripId + " not found"
            );
        }

        return placeRepository.findByTripId(tripId);
    }

    public Place getPlace(Long placeId) {
        return placeRepository.findById(placeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Place with id " + placeId + " not found"
                ));
    }

    public Place updatePlace(
            Long placeId,
            String name,
            String address,
            String description
    ) {
        Place place = getPlace(placeId);

        place.setName(name);
        place.setAddress(address);
        place.setDescription(description);

        return placeRepository.save(place);
    }

    public void deletePlace(Long placeId) {
        Place place = getPlace(placeId);

        placeRepository.delete(place);
    }
}