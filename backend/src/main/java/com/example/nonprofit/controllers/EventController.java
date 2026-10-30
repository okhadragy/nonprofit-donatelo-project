package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.DecoratedEventResponse;
import com.example.nonprofit.dto.EventCommunicationRequest;
import com.example.nonprofit.dto.EventCommunicationResponse;
import com.example.nonprofit.dto.EventRequest;
import com.example.nonprofit.dto.EventResponse;
import com.example.nonprofit.services.EventService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EventResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponse create(@RequestBody EventRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public EventResponse update(
            @PathVariable Long id,
            @RequestBody EventRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /**
     * Endpoint to demonstrate and inspect the dynamic GoF Decorator Pattern.
     */
    @GetMapping("/{id}/decorated")
    public DecoratedEventResponse getDecorated(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean fundraising,
            @RequestParam(defaultValue = "false") boolean reminder,
            @RequestParam(defaultValue = "false") boolean followup,
            @RequestParam(required = false) String fundraisingGoal,
            @RequestParam(required = false) String donationAppeal,
            @RequestParam(required = false) String reminderAlert,
            @RequestParam(required = false) String surveyUrl) {
        return service.getDecoratedEvent(
                id,
                fundraising,
                reminder,
                followup,
                fundraisingGoal,
                donationAppeal,
                reminderAlert,
                surveyUrl);
    }

    /**
     * Endpoint to send reminders and communications to registered attendees via EventObserver.
     */
    @PostMapping("/{id}/communications")
    public EventCommunicationResponse sendCommunication(
            @PathVariable Long id,
            @RequestBody(required = false) EventCommunicationRequest request) {
        return service.sendEventCommunication(id, request != null ? request : new EventCommunicationRequest());
    }

    @PostMapping("/{id}/send-reminder")
    public EventCommunicationResponse sendReminder(
            @PathVariable Long id,
            @RequestBody(required = false) EventCommunicationRequest request) {
        if (request == null) {
            request = new EventCommunicationRequest();
            request.setType("REMINDER");
        }
        return service.sendEventCommunication(id, request);
    }

    /**
     * Endpoint to view the live notification log from the EventSubject observer pipeline.
     */
    @GetMapping("/observer-logs")
    public List<String> getObserverLogs() {
        return service.getNotificationLogs();
    }
}
