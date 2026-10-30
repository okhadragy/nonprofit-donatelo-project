package com.example.nonprofit.event_observer;

import com.example.nonprofit.models.events.Event;

public interface EventObserver {

    void update(String message);

    void update(Event event, String action, String details);
}
