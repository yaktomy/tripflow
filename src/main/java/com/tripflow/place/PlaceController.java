package com.tripflow.place;

import com.tripflow.place.dto.CreatePlaceRequest;
import com.tripflow.place.dto.PlaceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips/{tripId}/places")
public class PlaceController {

    private final PlaceService placeService;
    private final PlaceMapper placeMapper;

    public PlaceController(
            PlaceService placeService,
            PlaceMapper placeMapper
    ) {
        this.placeService = placeService;
        this.placeMapper = placeMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceResponse createPlace(
            @PathVariable Long tripId,
            @Valid @RequestBody CreatePlaceRequest request
    ) {
        Place place = placeService.createPlace(
                tripId,
                request.name(),
                request.address(),
                request.description()
        );

        return placeMapper.toResponse(place);
    }

    @GetMapping
    public List<PlaceResponse> getPlaces(@PathVariable Long tripId) {
        return placeService.getPlaces(tripId)
                .stream()
                .map(placeMapper::toResponse)
                .toList();
    }

    @GetMapping("/{placeId}")
    public PlaceResponse getPlace(@PathVariable Long placeId) {
        return placeMapper.toResponse(
                placeService.getPlace(placeId)
        );
    }

    @PutMapping("/{placeId}")
    public PlaceResponse updatePlace(
            @PathVariable Long placeId,
            @Valid @RequestBody CreatePlaceRequest request
    ) {
        Place place = placeService.updatePlace(
                placeId,
                request.name(),
                request.address(),
                request.description()
        );

        return placeMapper.toResponse(place);
    }

    @DeleteMapping("/{placeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlace(@PathVariable Long placeId) {
        placeService.deletePlace(placeId);
    }
}