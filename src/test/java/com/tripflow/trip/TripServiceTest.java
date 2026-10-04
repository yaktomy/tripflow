package com.tripflow.trip;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.user.User;
import com.tripflow.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TripService tripService;

    @Test
    void shouldCreateTrip() {

        User user = new User(
                "testUser",
                "testUser@example.com"
        );

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        Trip savedTrip = new Trip(
                "Trip to Italy",
                "Rome and Florence"
        );

        when(tripRepository.save(any(Trip.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));;

        Trip result = tripService.createTrip(
                "Trip to Italy",
                "Rome and Florence",
                1L
        );

        assertEquals("Trip to Italy", result.getTitle());
        assertEquals("Rome and Florence", result.getDescription());
        assertEquals(user, result.getOwner());
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
}
