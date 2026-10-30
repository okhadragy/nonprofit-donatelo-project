package com.example.nonprofit.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.nonprofit.dto.EventRegistrationRequest;
import com.example.nonprofit.dto.EventRegistrationResponse;
import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.events.EventRegistration;
import com.example.nonprofit.models.events.Ticket;
import com.example.nonprofit.repositories.EventRegistrationRepository;
import com.example.nonprofit.repositories.EventRepository;
import com.example.nonprofit.repositories.TicketRepository;

@Service
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;

    public EventRegistrationService(
            EventRegistrationRepository registrationRepository,
            EventRepository eventRepository,
            TicketRepository ticketRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
    }

    public List<EventRegistrationResponse> getAll() {
        return registrationRepository.findAll().stream()
                .map(EventRegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<EventRegistrationResponse> getByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .map(EventRegistrationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public EventRegistrationResponse getById(Long id) {
        EventRegistration registration = findOrThrow(id);
        return EventRegistrationResponse.fromEntity(registration);
    }

    public EventRegistrationResponse create(EventRegistrationRequest request) {
        validateRequest(request);

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Event not found with id: " + request.getEventId()));

        Ticket ticket = null;
        if (request.getTicketId() != null) {
            ticket = ticketRepository.findById(request.getTicketId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Ticket not found with id: " + request.getTicketId()));
        }

        EventRegistration registration = new EventRegistration();
        registration.setAttendeeName(request.getAttendeeName());
        registration.setAttendanceStatus(
                request.getAttendanceStatus() != null ? request.getAttendanceStatus() : "Registered");
        registration.setEvent(event);
        registration.setTicket(ticket);

        EventRegistration saved = registrationRepository.save(registration);
        return EventRegistrationResponse.fromEntity(saved);
    }

    public EventRegistrationResponse update(Long id, EventRegistrationRequest request) {
        EventRegistration existing = findOrThrow(id);
        validateRequest(request);

        existing.setAttendeeName(request.getAttendeeName());
        existing.setAttendanceStatus(
                request.getAttendanceStatus() != null ? request.getAttendanceStatus() : existing.getAttendanceStatus());

        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Event not found with id: " + request.getEventId()));
            existing.setEvent(event);
        }

        if (request.getTicketId() != null) {
            Ticket ticket = ticketRepository.findById(request.getTicketId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Ticket not found with id: " + request.getTicketId()));
            existing.setTicket(ticket);
        }

        EventRegistration saved = registrationRepository.save(existing);
        return EventRegistrationResponse.fromEntity(saved);
    }

    public void delete(Long id) {
        EventRegistration existing = findOrThrow(id);
        registrationRepository.delete(existing);
    }

    private EventRegistration findOrThrow(Long id) {
        return registrationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event registration not found"));
    }

    private void validateRequest(EventRegistrationRequest request) {
        if (request.getAttendeeName() == null || request.getAttendeeName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Attendee name is required");
        }

        if (request.getEventId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event ID is required");
        }
    }
}
