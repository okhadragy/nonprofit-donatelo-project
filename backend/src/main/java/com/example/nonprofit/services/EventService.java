package com.example.nonprofit.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.nonprofit.dto.DecoratedEventResponse;
import com.example.nonprofit.dto.EventCommunicationRequest;
import com.example.nonprofit.dto.EventCommunicationResponse;
import com.example.nonprofit.dto.EventRequest;
import com.example.nonprofit.dto.EventResponse;
import com.example.nonprofit.event_decorator.EventComponent;
import com.example.nonprofit.event_decorator.FollowUpDecorator;
import com.example.nonprofit.event_decorator.FundraisingDecorator;
import com.example.nonprofit.event_decorator.ReminderDecorator;
import com.example.nonprofit.event_observer.EventSubject;
import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.events.EventCategory;
import com.example.nonprofit.models.events.EventRegistration;
import com.example.nonprofit.repositories.EventCategoryRepository;
import com.example.nonprofit.repositories.EventRegistrationRepository;
import com.example.nonprofit.repositories.EventRepository;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventCategoryRepository categoryRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventSubject eventSubject;

    public EventService(
            EventRepository eventRepository,
            EventCategoryRepository categoryRepository,
            EventRegistrationRepository registrationRepository,
            EventSubject eventSubject) {
        this.eventRepository = eventRepository;
        this.categoryRepository = categoryRepository;
        this.registrationRepository = registrationRepository;
        this.eventSubject = eventSubject;
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
        eventSubject.notifyEventChanged(
                saved,
                "CREATED",
                "New event created: '" + saved.getTitle() + "' scheduled on " + saved.getDate());
        return EventResponse.fromEntity(saved);
    }

    public EventResponse update(Long id, EventRequest request) {
        Event existing = findEventOrThrow(id);
        validateRequest(request);

        String oldStatus = existing.getStatus();
        applyRequestToEntity(request, existing);

        Event saved = eventRepository.save(existing);

        if (oldStatus != null && !oldStatus.equalsIgnoreCase(saved.getStatus())) {
            eventSubject.notifyEventChanged(
                    saved,
                    "STATUS_CHANGE",
                    "Status changed from '" + oldStatus + "' to '" + saved.getStatus() + "'");
            if (saved.getStatus().equalsIgnoreCase("Cancelled")) {
                eventSubject.notifyEventChanged(
                        saved,
                        "CANCELLED",
                        "Event has been cancelled. Attendee registrations notified.");
            }
        } else {
            eventSubject.notifyEventChanged(
                    saved,
                    "UPDATED",
                    "Event details updated for '" + saved.getTitle() + "'");
        }

        return EventResponse.fromEntity(saved);
    }

    public void delete(Long id) {
        Event existing = findEventOrThrow(id);
        eventSubject.notifyEventChanged(
                existing,
                "DELETED",
                "Event '" + existing.getTitle() + "' has been deleted from system.");
        eventRepository.delete(existing);
    }

    /**
     * Demonstrates dynamic runtime composition of the Gang of Four (GoF) Decorator Pattern.
     * Wraps the base Event component with any requested combination of Fundraising,
     * Reminder, and FollowUp decorators.
     */
    public DecoratedEventResponse getDecoratedEvent(
            Long id,
            boolean includeFundraising,
            boolean includeReminder,
            boolean includeFollowUp,
            String customGoal,
            String customAppeal,
            String customReminderAlert,
            String customSurveyUrl) {
        Event event = findEventOrThrow(id);
        String baseDescription = event.getDescription();

        EventComponent component = event;
        List<String> applied = new ArrayList<>();

        if (includeFundraising) {
            component = new FundraisingDecorator(
                    component,
                    customGoal,
                    customAppeal,
                    null);
            applied.add("FundraisingDecorator");
        }

        if (includeReminder) {
            component = new ReminderDecorator(
                    component,
                    customReminderAlert,
                    null);
            applied.add("ReminderDecorator");
        }

        if (includeFollowUp) {
            component = new FollowUpDecorator(
                    component,
                    customSurveyUrl,
                    null);
            applied.add("FollowUpDecorator");
        }

        return new DecoratedEventResponse(
                event.getId(),
                event.getTitle(),
                baseDescription,
                applied,
                component.getDescription());
    }

    /**
     * Dispatches communications / reminders to attendees using the Observer Pattern.
     */
    public EventCommunicationResponse sendEventCommunication(
            Long eventId,
            EventCommunicationRequest request) {
        Event event = findEventOrThrow(eventId);
        List<EventRegistration> registrations = registrationRepository.findByEventId(eventId);

        String message = (request != null && request.getMessage() != null && !request.getMessage().isBlank())
                ? request.getMessage()
                : "Reminder: Event '" + event.getTitle() + "' is scheduled for "
                + (event.getDate() != null ? event.getDate().toString() : "soon")
                + " at " + (event.getLocation() != null ? event.getLocation() : "TBD")
                + ". We look forward to seeing you!";

        String subject = (request != null && request.getSubject() != null && !request.getSubject().isBlank())
                ? request.getSubject()
                : "Event Reminder: " + event.getTitle();

        String type = (request != null && request.getType() != null && !request.getType().isBlank())
                ? request.getType()
                : "REMINDER";

        List<String> recipientNames = registrations.stream()
                .map(EventRegistration::getAttendeeName)
                .collect(Collectors.toList());

        for (EventRegistration reg : registrations) {
            eventSubject.notifyEventChanged(
                    event,
                    "ATTENDEE_" + type,
                    "Dispatched to " + reg.getAttendeeName() + " | Subject: " + subject + " | Message: " + message);
        }

        if (registrations.isEmpty()) {
            eventSubject.notifyEventChanged(
                    event,
                    "BROADCAST_" + type,
                    "Broadcast reminder queued (No attendees currently registered) | Subject: " + subject + " | Message: " + message);
        }

        return new EventCommunicationResponse(
                event.getId(),
                event.getTitle(),
                subject,
                message,
                type,
                recipientNames.size(),
                recipientNames,
                new Date());
    }

    public List<String> getNotificationLogs() {
        return eventSubject.getNotificationLog();
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
