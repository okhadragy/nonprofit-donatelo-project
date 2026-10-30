package com.example.nonprofit.event_observer;

import com.example.nonprofit.models.events.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventObserverPatternTest {

    private EventSubject eventSubject;
    private List<String> receivedMessages;
    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        receivedMessages = new ArrayList<>();
        eventSubject = new EventSubject(new ArrayList<>());
        sampleEvent = new Event("Outreach Program 2026", new Date(), "Alexandria", "Planned", null);
    }

    @Test
    void testObserverRegistrationAndNotification() {
        EventObserver testObserver = new EventObserver() {
            @Override
            public void update(String message) {
                receivedMessages.add(message);
            }

            @Override
            public void update(Event event, String action, String details) {
                receivedMessages.add(event.getTitle() + " - " + action + ": " + details);
            }
        };

        eventSubject.addObserver(testObserver);
        eventSubject.notifyEventChanged(sampleEvent, "STATUS_CHANGE", "Changed to Ongoing");

        assertEquals(1, receivedMessages.size());
        assertTrue(receivedMessages.get(0).contains("Outreach Program 2026"));
        assertTrue(receivedMessages.get(0).contains("STATUS_CHANGE"));
        assertTrue(receivedMessages.get(0).contains("Changed to Ongoing"));
    }

    @Test
    void testObserverRemoval() {
        EventObserver testObserver = new EventObserver() {
            @Override
            public void update(String message) {
                receivedMessages.add(message);
            }

            @Override
            public void update(Event event, String action, String details) {
                receivedMessages.add(details);
            }
        };

        eventSubject.addObserver(testObserver);
        eventSubject.removeObserver(testObserver);

        eventSubject.notifyEventChanged(sampleEvent, "CANCELLED", "Event was cancelled");
        assertTrue(receivedMessages.isEmpty());
    }

    @Test
    void testNotificationLogHistory() {
        eventSubject.notifyEventChanged(sampleEvent, "CREATED", "Created event");
        eventSubject.notifyEventChanged(sampleEvent, "ATTENDEE_REMINDER", "Reminder dispatched");

        List<String> log = eventSubject.getNotificationLog();
        assertEquals(2, log.size());
        assertTrue(log.get(0).contains("CREATED"));
        assertTrue(log.get(1).contains("ATTENDEE_REMINDER"));
    }
}
