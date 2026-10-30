package com.example.nonprofit.event_observer;

import com.example.nonprofit.models.events.Event;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class EventSubject implements Subject {

    private final List<EventObserver> observers = new ArrayList<>();
    private final List<String> notificationLog = Collections.synchronizedList(new ArrayList<>());
    private String message;

    public EventSubject(List<EventObserver> observers) {
        this.observers.addAll(observers);
    }

    @Override
    public void addObserver(EventObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(EventObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        if (message != null) {
            notificationLog.add(message);
            for (EventObserver observer : observers) {
                observer.update(message);
            }
        }
    }

    public void setMessage(String message) {
        this.message = message;
        notifyObservers();
    }

    public void notifyEventChanged(Event event, String action, String details) {
        String logEntry = "[Event #" + event.getId() + " - " + event.getTitle() + "] [" + action + "] " + details;
        notificationLog.add(logEntry);
        for (EventObserver observer : observers) {
            observer.update(event, action, details);
        }
    }

    public List<String> getNotificationLog() {
        return new ArrayList<>(notificationLog);
    }
}
