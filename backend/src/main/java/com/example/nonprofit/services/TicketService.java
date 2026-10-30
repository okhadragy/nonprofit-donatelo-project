package com.example.nonprofit.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.nonprofit.dto.TicketRequest;
import com.example.nonprofit.dto.TicketResponse;
import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.events.Ticket;
import com.example.nonprofit.repositories.EventRepository;
import com.example.nonprofit.repositories.TicketRepository;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository) {
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
    }

    public List<TicketResponse> getAll() {
        return ticketRepository.findAll().stream()
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TicketResponse> getByEventId(Long eventId) {
        return ticketRepository.findByEventId(eventId).stream()
                .map(TicketResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public TicketResponse getById(Long id) {
        Ticket ticket = findTicketOrThrow(id);
        return TicketResponse.fromEntity(ticket);
    }

    public TicketResponse create(TicketRequest request) {
        validateRequest(request);

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Event not found with id: " + request.getEventId()));

        Ticket ticket = new Ticket();
        ticket.setName(request.getName());
        ticket.setPrice(request.getPrice());
        ticket.setEvent(event);

        Ticket saved = ticketRepository.save(ticket);
        return TicketResponse.fromEntity(saved);
    }

    public TicketResponse update(Long id, TicketRequest request) {
        Ticket existing = findTicketOrThrow(id);
        validateRequest(request);

        existing.setName(request.getName());
        existing.setPrice(request.getPrice());

        if (request.getEventId() != null) {
            Event event = eventRepository.findById(request.getEventId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Event not found with id: " + request.getEventId()));
            existing.setEvent(event);
        }

        Ticket saved = ticketRepository.save(existing);
        return TicketResponse.fromEntity(saved);
    }

    public void delete(Long id) {
        Ticket existing = findTicketOrThrow(id);
        ticketRepository.delete(existing);
    }

    private Ticket findTicketOrThrow(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    private void validateRequest(TicketRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ticket name is required");
        }

        if (request.getPrice() == null || request.getPrice().signum() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Ticket price must be zero or positive");
        }

        if (request.getEventId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event ID is required");
        }
    }
}
