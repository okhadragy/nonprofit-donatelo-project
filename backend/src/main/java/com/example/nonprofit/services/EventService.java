package com.example.nonprofit.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.nonprofit.dto.EventRequest;
import com.example.nonprofit.dto.EventResponse;
import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.events.EventCategory;
import com.example.nonprofit.repositories.EventCategoryRepository;
import com.example.nonprofit.repositories.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;

    public EventService(
            EventRepository eventRepository,
            EventCategoryRepository categoryRepository) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<EventResponse> getAll() {
        return eventRepository.findAll().stream()
                .map(EventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public EventResponse getById(Long id) {
        Event event = findEventOrThrow(id);
        return EventResponse.fromEntity(event);
    }

    public EventResponse create(EventRequest request) {
        validateRequest(request);

        Event event = new Event();
        applyRequestToEntity(request, event);

        Event saved = eventRepository.save(event);
        return EventResponse.fromEntity(saved);
    }

    public EventResponse update(Long id, EventRequest request) {
        Event existing = findEventOrThrow(id);
        validateRequest(request);
        applyRequestToEntity(request, existing);

        Event saved = eventRepository.save(existing);
        return EventResponse.fromEntity(saved);
    }

    public void delete(Long id) {
        Event existing = findEventOrThrow(id);
        eventRepository.delete(existing);
    }

    private Event findEventOrThrow(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event not found"));
    }

    private void validateRequest(EventRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event title is required");
        }

        if (request.getDate() == null || request.getDate().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event date is required");
        }

        if (request.getStatus() == null || request.getStatus().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event status is required");
        }
    }

    private void applyRequestToEntity(EventRequest request, Event event) {
        event.setTitle(request.getTitle());
        event.setLocation(request.getLocation());
        event.setStatus(request.getStatus());

        // Parse date string to Date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date date = sdf.parse(request.getDate());
            event.setDate(date);
        } catch (ParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format. Use yyyy-MM-dd");
        }

        // Resolve event category
        if (request.getCategoryId() != null) {
            EventCategory category = categoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Event category not found with id: " + request.getCategoryId()));
            event.setCategory(category);
        } else {
            event.setCategory(null);
        }
    }
}
