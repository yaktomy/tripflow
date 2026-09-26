package com.tripflow.event;

import com.tripflow.common.exception.ResourceNotFoundException;
import com.tripflow.trip.Trip;
import com.tripflow.trip.TripRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final TripRepository tripRepository;

    public EventService(
            EventRepository eventRepository,
            TripRepository tripRepository
    ) {
        this.eventRepository = eventRepository;
        this.tripRepository = tripRepository;
    }

    public Event createEvent(
            Long tripId,
            String title,
            String description,
            java.time.LocalDateTime startTime
    ) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip with id " + tripId + " not found"
                ));

        Event event = new Event(
                title,
                description,
                startTime,
                trip
        );

        return eventRepository.save(event);
    }

    public List<Event> getEvents(Long tripId) {
        if (!tripRepository.existsById(tripId)) {
            throw new ResourceNotFoundException(
                    "Trip with id " + tripId + " not found"
            );
        }

        return eventRepository.findByTripId(tripId);
    }

    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event with id " + eventId + " not found"
                ));
    }

    public Event updateEvent(
            Long eventId,
            String title,
            String description,
            java.time.LocalDateTime startTime
    ) {
        Event event = getEvent(eventId);

        event.setTitle(title);
        event.setDescription(description);
        event.setStartTime(startTime);

        return eventRepository.save(event);
    }

    public void deleteEvent(Long eventId) {
        Event event = getEvent(eventId);
        eventRepository.delete(event);
    }
}