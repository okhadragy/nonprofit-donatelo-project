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
import com.example.nonprofit.models.users.User;
import com.example.nonprofit.repositories.EventRegistrationRepository;
import com.example.nonprofit.repositories.EventRepository;
import com.example.nonprofit.repositories.TicketRepository;
import com.example.nonprofit.repositories.UserRepository;

@Service
public class EventRegistrationService {

    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public EventRegistrationService(
            EventRegistrationRepository registrationRepository,
            EventRepository eventRepository,
            TicketRepository ticketRepository,
            UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
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

        Ticket ticket = resolveTicket(request.getTicketId(), event);

        EventRegistration registration = new EventRegistration();
        registration.setUser(resolveUser(request.getUserId()));
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

        existing.setUser(resolveUser(request.getUserId()));
        existing.setAttendanceStatus(
                request.getAttendanceStatus() != null ? request.getAttendanceStatus() : existing.getAttendanceStatus());

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Event not found with id: " + request.getEventId()));
        existing.setEvent(event);
        existing.setTicket(resolveTicket(request.getTicketId(), event));

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

    /** The attendee must be an existing user. */
    private User resolveUser(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "User is required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "User not found with id: " + userId));
    }

    /** A ticket is required and must belong to the registration's event. */
    private Ticket resolveTicket(Long ticketId, Event event) {
        if (ticketId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ticket is required");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Ticket not found with id: " + ticketId));
        if (ticket.getEvent() == null || !ticket.getEvent().getId().equals(event.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ticket does not belong to the selected event");
        }
        return ticket;
    }

    private void validateRequest(EventRegistrationRequest request) {
        if (request.getEventId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event ID is required");
        }
    }
}
