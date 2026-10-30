package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.EventRegistrationRequest;
import com.example.nonprofit.dto.EventRegistrationResponse;
import com.example.nonprofit.services.EventRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-registrations")
public class EventRegistrationController {

    private final EventRegistrationService service;

    public EventRegistrationController(EventRegistrationService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventRegistrationResponse> getAll(@RequestParam(required = false) Long eventId) {
        if (eventId != null) {
            return service.getByEventId(eventId);
        }
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EventRegistrationResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/event/{eventId}")
    public List<EventRegistrationResponse> getByEventId(@PathVariable Long eventId) {
        return service.getByEventId(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventRegistrationResponse create(@RequestBody EventRegistrationRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public EventRegistrationResponse update(
            @PathVariable Long id,
            @RequestBody EventRegistrationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
