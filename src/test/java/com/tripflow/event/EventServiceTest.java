package com.tripflow.event;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.trip.Trip;
import com.tripflow.trip.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TripRepository tripRepository;

    @InjectMocks
    private EventService eventService;

    private Trip trip;

    @BeforeEach
    void setUp() {
        trip = new Trip(
                "Trip to Italy",
                "Rome and Florence"
        );
    }

    @Test
    void shouldCreateEvent() {
        LocalDateTime startTime = LocalDateTime.of(
                2026, 10, 5, 14, 0
        );

        when(tripRepository.findById(1L))
                .thenReturn(Optional.of(trip));

        Event savedEvent = new Event(
                "Visit Colosseum",
                "Guided tour",
                startTime,
                trip
        );

        when(eventRepository.save(any(Event.class)))
                .thenReturn(savedEvent);

        Event result = eventService.createEvent(
                1L,
                "Visit Colosseum",
                "Guided tour",
                startTime
        );

        assertEquals("Visit Colosseum", result.getTitle());
        assertEquals("Guided tour", result.getDescription());
        assertEquals(startTime, result.getStartTime());
        assertEquals(trip, result.getTrip());

        verify(tripRepository).findById(1L);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void shouldThrowExceptionWhenTripNotFound() {
        LocalDateTime startTime = LocalDateTime.of(
                2026, 10, 5, 14, 0
        );

        when(tripRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.createEvent(
                        999L,
                        "Visit Colosseum",
                        "Guided tour",
                        startTime
                )
        );

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void shouldReturnEvents() {
        Event event1 = new Event(
                "Visit Colosseum",
                "Guided tour",
                LocalDateTime.of(2026, 10, 5, 14, 0),
                trip
        );

        Event event2 = new Event(
                "Visit Vatican",
                "Museum tour",
                LocalDateTime.of(2026, 10, 6, 10, 0),
                trip
        );

        when(tripRepository.existsById(1L))
                .thenReturn(true);

        when(eventRepository.findByTripId(1L))
                .thenReturn(List.of(event1, event2));

        List<Event> result = eventService.getEvents(1L);

        assertEquals(2, result.size());
        assertEquals("Visit Colosseum", result.get(0).getTitle());
        assertEquals("Visit Vatican", result.get(1).getTitle());

        verify(tripRepository).existsById(1L);
        verify(eventRepository).findByTripId(1L);
    }

    @Test
    void shouldThrowExceptionWhenGettingEventsForNonExistingTrip() {
        when(tripRepository.existsById(999L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.getEvents(999L)
        );

        verify(eventRepository, never()).findByTripId(999L);
    }

    @Test
    void shouldReturnEventById() {
        Event event = new Event(
                "Visit Colosseum",
                "Guided tour",
                LocalDateTime.of(2026, 10, 5, 14, 0),
                trip
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        Event result = eventService.getEvent(1L);

        assertEquals("Visit Colosseum", result.getTitle());
        assertEquals("Guided tour", result.getDescription());

        verify(eventRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenEventNotFound() {
        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.getEvent(999L)
        );

        verify(eventRepository).findById(999L);
    }

    @Test
    void shouldUpdateEvent() {
        Event event = new Event(
                "Old event",
                "Old description",
                LocalDateTime.of(2026, 10, 5, 10, 0),
                trip
        );

        LocalDateTime newStartTime = LocalDateTime.of(
                2026, 10, 5, 16, 0
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        when(eventRepository.save(event))
                .thenReturn(event);

        Event result = eventService.updateEvent(
                1L,
                "New event",
                "New description",
                newStartTime
        );

        assertEquals("New event", result.getTitle());
        assertEquals("New description", result.getDescription());
        assertEquals(newStartTime, result.getStartTime());

        verify(eventRepository).findById(1L);
        verify(eventRepository).save(event);
    }

    @Test
    void shouldDeleteEvent() {
        Event event = new Event(
                "Visit Colosseum",
                "Guided tour",
                LocalDateTime.of(2026, 10, 5, 14, 0),
                trip
        );

        when(eventRepository.findById(1L))
                .thenReturn(Optional.of(event));

        eventService.deleteEvent(1L);

        verify(eventRepository).findById(1L);
        verify(eventRepository).delete(event);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEvent() {
        when(eventRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> eventService.deleteEvent(999L)
        );

        verify(eventRepository, never()).delete(any(Event.class));
    }
}