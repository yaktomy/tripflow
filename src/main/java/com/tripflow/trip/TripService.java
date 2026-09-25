package com.tripflow.trip;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.user.User;
import com.tripflow.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public TripService(
            TripRepository tripRepository,
            UserRepository userRepository
    ) {
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    public Trip createTrip(
            String title,
            String description,
            Long ownerId
    ) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + ownerId + " not found"
                ));

        Trip trip = new Trip(title, description);
        trip.setOwner(owner);

        return tripRepository.save(trip);
    }

    public List<Trip> getTrips() {
        return tripRepository.findAll();
    }

    public Trip getTrip(Long id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip with id " + id + " not found"
                ));
    }

    public Trip updateTrip(Long id, String title, String description) {
        Trip trip = getTrip(id);

        trip.setTitle(title);
        trip.setDescription(description);

        return tripRepository.save(trip);
    }

    public void deleteTrip(Long id) {
        Trip trip = getTrip(id);
        tripRepository.delete(trip);
    }
}