package com.example.nonprofit.event_decorator;

import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.events.EventCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DecoratorPatternTest {

    private Event baseEvent;

    @BeforeEach
    void setUp() {
        EventCategory category = new EventCategory("Fundraiser");
        baseEvent = new Event("Charity Gala 2026", new Date(), "Cairo Opera House", "Planned", category);
    }

    @Test
    void testBaseEventComponent() {
        assertNotNull(baseEvent.getDescription());
        assertTrue(baseEvent.getDescription().contains("Charity Gala 2026"));
        assertTrue(baseEvent.getDescription().contains("Cairo Opera House"));
    }

    @Test
    void testFundraisingDecorator() {
        EventComponent decorated = new FundraisingDecorator(
                baseEvent,
                "$100,000",
                "Support health clinic supplies",
                "Tier 1: Plaque | Tier 2: VIP"
        );

        String description = decorated.getDescription();
        assertTrue(description.contains("Charity Gala 2026"));
        assertTrue(description.contains("[FUNDRAISING GOALS & REWARDS]"));
        assertTrue(description.contains("$100,000"));
        assertTrue(description.contains("Support health clinic supplies"));
    }

    @Test
    void testReminderDecorator() {
        EventComponent decorated = new ReminderDecorator(
                baseEvent,
                "Doors open at 6 PM sharp",
                "Scan your QR badge at Reception Desk B"
        );

        String description = decorated.getDescription();
        assertTrue(description.contains("Charity Gala 2026"));
        assertTrue(description.contains("[SCHEDULE ALERTS & REMINDERS]"));
        assertTrue(description.contains("Doors open at 6 PM sharp"));
        assertTrue(description.contains("Scan your QR badge at Reception Desk B"));
    }

    @Test
    void testFollowUpDecorator() {
        EventComponent decorated = new FollowUpDecorator(
                baseEvent,
                "https://nonprofit.org/gala-survey",
                "Thank you for attending the 2026 Gala!"
        );

        String description = decorated.getDescription();
        assertTrue(description.contains("Charity Gala 2026"));
        assertTrue(description.contains("[POST-EVENT FOLLOW-UP & SURVEY]"));
        assertTrue(description.contains("https://nonprofit.org/gala-survey"));
        assertTrue(description.contains("Thank you for attending the 2026 Gala!"));
    }

    @Test
    void testDynamicChainingOfAllDecorators() {
        // Base Event -> FundraisingDecorator -> ReminderDecorator -> FollowUpDecorator
        EventComponent fullyDecorated = new FollowUpDecorator(
                new ReminderDecorator(
                        new FundraisingDecorator(baseEvent, "$75,000", "Winter aid", "Gold / Silver"),
                        "Arrive 15 min early",
                        "Bring ID"
                ),
                "https://survey.link",
                "Thanks for coming"
        );

        String description = fullyDecorated.getDescription();
        assertTrue(description.contains("Charity Gala 2026"));
        assertTrue(description.contains("[FUNDRAISING GOALS & REWARDS]"));
        assertTrue(description.contains("[SCHEDULE ALERTS & REMINDERS]"));
        assertTrue(description.contains("[POST-EVENT FOLLOW-UP & SURVEY]"));
        assertTrue(description.contains("$75,000"));
        assertTrue(description.contains("Arrive 15 min early"));
        assertTrue(description.contains("Thanks for coming"));
    }
}

