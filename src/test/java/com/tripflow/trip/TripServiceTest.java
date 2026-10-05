package com.tripflow.trip;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.user.User;
import com.tripflow.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripService tripService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                "marselina",
                "marselina@example.com"
        );
    }

    @Test
    void shouldCreateTrip() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Trip savedTrip = new Trip(
                "Trip to Italy",
                "Rome and Florence"
        );
        savedTrip.setOwner(user);

        when(tripRepository.save(any(Trip.class)))
                .thenReturn(savedTrip);

        Trip result = tripService.createTrip(
                "Trip to Italy",
                "Rome and Florence",
                1L
        );

        assertEquals("Trip to Italy", result.getTitle());
        assertEquals("Rome and Florence", result.getDescription());
        assertEquals(user, result.getOwner());

        verify(userRepository).findById(1L);
        verify(tripRepository).save(any(Trip.class));
    }

    @Test
    void shouldThrowExceptionWhenOwnerNotFound() {
        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tripService.createTrip(
                        "Trip to Italy",
                        "Rome and Florence",
                        999L
                )
        );

        verify(tripRepository, never()).save(any(Trip.class));
    }

    @Test
    void shouldReturnAllTrips() {
        Trip trip1 = new Trip("Italy", "Rome");
        Trip trip2 = new Trip("France", "Paris");

        when(tripRepository.findAll())
                .thenReturn(List.of(trip1, trip2));

        List<Trip> result = tripService.getTrips();

        assertEquals(2, result.size());
        assertEquals("Italy", result.get(0).getTitle());
        assertEquals("France", result.get(1).getTitle());

        verify(tripRepository).findAll();
    }

    @Test
    void shouldReturnTripById() {
        Trip trip = new Trip(
                "Trip to Italy",
                "Rome and Florence"
        );

        when(tripRepository.findById(1L))
                .thenReturn(Optional.of(trip));

        Trip result = tripService.getTrip(1L);

        assertEquals("Trip to Italy", result.getTitle());
        assertEquals("Rome and Florence", result.getDescription());

        verify(tripRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenTripNotFound() {
        when(tripRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tripService.getTrip(999L)
        );

        verify(tripRepository).findById(999L);
    }

    @Test
    void shouldUpdateTrip() {
        Trip trip = new Trip(
                "Old title",
                "Old description"
        );

        when(tripRepository.findById(1L))
                .thenReturn(Optional.of(trip));

        when(tripRepository.save(trip))
                .thenReturn(trip);

        Trip result = tripService.updateTrip(
                1L,
                "New title",
                "New description"
        );

        assertEquals("New title", result.getTitle());
        assertEquals("New description", result.getDescription());

        verify(tripRepository).findById(1L);
        verify(tripRepository).save(trip);
    }

    @Test
    void shouldDeleteTrip() {
        Trip trip = new Trip(
                "Trip to Italy",
                "Rome and Florence"
        );

        when(tripRepository.findById(1L))
                .thenReturn(Optional.of(trip));

        tripService.deleteTrip(1L);

        verify(tripRepository).findById(1L);
        verify(tripRepository).delete(trip);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingTrip() {
        when(tripRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> tripService.deleteTrip(999L)
        );

        verify(tripRepository, never()).delete(any(Trip.class));
    }
}