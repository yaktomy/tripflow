package com.tripflow.trip;

import com.tripflow.trip.dto.CreateTripRequest;
import com.tripflow.trip.dto.TripResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    private final TripService tripService;
    private final TripMapper tripMapper;

    public TripController(TripService tripService, TripMapper tripMapper) {
        this.tripService = tripService;
        this.tripMapper = tripMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TripResponse createTrip(@Valid @RequestBody CreateTripRequest request) {
        Trip trip = tripService.createTrip(
                request.title(),
                request.description(),
                request.ownerId()
        );

        return tripMapper.toResponse(trip);
    }

    @GetMapping
    public List<TripResponse> getTrips() {
        return tripService.getTrips()
                .stream()
                .map(tripMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public TripResponse getTrip(@PathVariable Long id) {
        return tripMapper.toResponse(tripService.getTrip(id));
    }

    @PutMapping("/{id}")
    public TripResponse updateTrip(
            @PathVariable Long id,
            @Valid @RequestBody CreateTripRequest request
    ) {
        Trip trip = tripService.updateTrip(
                id,
                request.title(),
                request.description()
        );

        return tripMapper.toResponse(trip);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
    }
}