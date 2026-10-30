package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.TicketRequest;
import com.example.nonprofit.dto.TicketResponse;
import com.example.nonprofit.services.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @GetMapping
    public List<TicketResponse> getAll(@RequestParam(required = false) Long eventId) {
        if (eventId != null) {
            return service.getByEventId(eventId);
        }
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TicketResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/event/{eventId}")
    public List<TicketResponse> getByEventId(@PathVariable Long eventId) {
        return service.getByEventId(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(@RequestBody TicketRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public TicketResponse update(
            @PathVariable Long id,
            @RequestBody TicketRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
