package com.example.nonprofit.event_observer;

import com.example.nonprofit.models.events.Event;
import org.springframework.stereotype.Component;

@Component
public class EventEmailNotifier implements EventObserver {

    @Override
    public void update(String message) {
        System.out.println("[EventEmailNotifier] Dispatching email notice: " + message);
    }

    @Override
    public void update(Event event, String action, String details) {
        System.out.println("[EventEmailNotifier] Email sent to attendees of event #" + event.getId()
                + " ('" + event.getTitle() + "') | Action: " + action + " | Details: " + details);
    }
}

