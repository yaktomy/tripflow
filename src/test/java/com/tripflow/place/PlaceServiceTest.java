package com.tripflow.place;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.trip.Trip;
import com.tripflow.trip.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private PlaceService placeService;

    private Trip trip;

    @BeforeEach
    void setUp() {
        trip = new Trip(
                "Trip to Italy",
                "Rome and Florence"
        );
    }

    @Test
    void shouldCreatePlace() {
        when(tripRepository.findById(1L))
                .thenReturn(Optional.of(trip));

        Place savedPlace = new Place(
                "Colosseum",
                "Piazza del Colosseo, 1",
                "Ancient Roman amphitheatre",
                trip
        );

        when(placeRepository.save(any(Place.class)))
                .thenReturn(savedPlace);

        Place result = placeService.createPlace(
                1L,
                "Colosseum",
                "Piazza del Colosseo, 1",
                "Ancient Roman amphitheatre"
        );

        assertEquals("Colosseum", result.getName());
        assertEquals("Piazza del Colosseo, 1", result.getAddress());
        assertEquals("Ancient Roman amphitheatre", result.getDescription());
        assertEquals(trip, result.getTrip());

        verify(tripRepository).findById(1L);
        verify(placeRepository).save(any(Place.class));
    }

    @Test
    void shouldThrowExceptionWhenTripNotFound() {
        when(tripRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> placeService.createPlace(
                        999L,
                        "Colosseum",
                        "Rome",
                        "Ancient amphitheatre"
                )
        );

        verify(placeRepository, never()).save(any(Place.class));
    }

    @Test
    void shouldReturnPlaces() {
        Place place1 = new Place(
                "Colosseum",
                "Rome",
                "Ancient amphitheatre",
                trip
        );

        Place place2 = new Place(
                "Vatican Museums",
                "Vatican City",
                "Famous museum complex",
                trip
        );

        when(tripRepository.existsById(1L))
                .thenReturn(true);

        when(placeRepository.findByTripId(1L))
                .thenReturn(List.of(place1, place2));

        List<Place> result = placeService.getPlaces(1L);

        assertEquals(2, result.size());
        assertEquals("Colosseum", result.get(0).getName());
        assertEquals("Vatican Museums", result.get(1).getName());

        verify(tripRepository).existsById(1L);
        verify(placeRepository).findByTripId(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingPlacesForNonExistingTrip() {
        when(tripRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> placeService.getPlaces(999L)
        );

        verify(placeRepository, never())
                .findByTripId(999L);
    }

    @Test
    void shouldReturnPlaceById() {
        Place place = new Place(
                "Colosseum",
                "Rome",
                "Ancient amphitheatre",
                trip
        );

        when(placeRepository.findById(1L))
                .thenReturn(Optional.of(place));

        Place result = placeService.getPlace(1L);

        assertEquals("Colosseum", result.getName());
        assertEquals("Rome", result.getAddress());

        verify(placeRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenPlaceNotFound() {
        when(placeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> placeService.getPlace(999L)
        );

        verify(placeRepository).findById(999L);
    }

    @Test
    void shouldUpdatePlace() {
        Place place = new Place(
                "Old Place",
                "Old address",
                "Old description",
                trip
        );

        when(placeRepository.findById(1L))
                .thenReturn(Optional.of(place));

        when(placeRepository.save(place))
                .thenReturn(place);

        Place result = placeService.updatePlace(
                1L,
                "New Place",
                "New address",
                "New description"
        );

        assertEquals("New Place", result.getName());
        assertEquals("New address", result.getAddress());
        assertEquals("New description", result.getDescription());

        verify(placeRepository).findById(1L);
        verify(placeRepository).save(place);
    }

    @Test
    void shouldDeletePlace() {
        Place place = new Place(
                "Colosseum",
                "Rome",
                "Ancient amphitheatre",
                trip
        );

        when(placeRepository.findById(1L))
                .thenReturn(Optional.of(place));

        placeService.deletePlace(1L);

        verify(placeRepository).findById(1L);
        verify(placeRepository).delete(place);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingPlace() {
        when(placeRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> placeService.deletePlace(999L)
        );

        verify(placeRepository, never())
                .delete(any(Place.class));
    }
}