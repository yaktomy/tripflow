package com.tripflow.event;

import com.tripflow.event.dto.CreateEventRequest;
import com.tripflow.event.dto.EventResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips/{tripId}/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper mapper;

    public EventController(EventService eventService, EventMapper mapper) {
        this.eventService = eventService;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse createEvent(
            @PathVariable Long tripId,
            @Valid @RequestBody CreateEventRequest request
    ) {
        Event event = eventService.createEvent(
                tripId,
                request.title(),
                request.description(),
                request.startTime()
        );

        return mapper.toResponse(event);
    }

    @GetMapping
    public List<EventResponse> getEvents(@PathVariable Long tripId) {
        return eventService.getEvents(tripId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @GetMapping("/{eventId}")
    public EventResponse getEvent(@PathVariable Long eventId) {
        return mapper.toResponse(eventService.getEvent(eventId));
    }

    @PutMapping("/{eventId}")
    public EventResponse updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateEventRequest request
    ) {
        Event event = eventService.updateEvent(
                eventId,
                request.title(),
                request.description(),
                request.startTime()
        );

        return mapper.toResponse(event);
    }

    @DeleteMapping("/{eventId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable Long eventId) {
        eventService.deleteEvent(eventId);
    }

}