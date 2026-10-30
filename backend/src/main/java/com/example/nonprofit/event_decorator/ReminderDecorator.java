package com.example.nonprofit.event_decorator;

public class ReminderDecorator extends EventDecorator {

    private final String scheduleAlert;
    private final String checkInInstructions;

    public ReminderDecorator(EventComponent decoratedEvent) {
        this(
            decoratedEvent,
            "Doors open 30 minutes prior to scheduled start time.",
            "Please present your registration QR badge or confirmation email at the reception desk for fast check-in."
        );
    }

    public ReminderDecorator(
            EventComponent decoratedEvent,
            String scheduleAlert,
            String checkInInstructions) {
        super(decoratedEvent);
        this.scheduleAlert = (scheduleAlert != null && !scheduleAlert.isBlank())
                ? scheduleAlert
                : "Doors open 30 minutes prior to scheduled start time.";
        this.checkInInstructions = (checkInInstructions != null && !checkInInstructions.isBlank())
                ? checkInInstructions
                : "Please present your registration QR badge or confirmation email at the reception desk for fast check-in.";
    }

    @Override
    public String getDescription() {
        return super.getDescription()
                + "\n\n--- [SCHEDULE ALERTS & REMINDERS] ---"
                + "\nSchedule Alert: " + scheduleAlert
                + "\nCheck-in Instructions: " + checkInInstructions;
    }

    public String getScheduleAlert() {
        return scheduleAlert;
    }

    public String getCheckInInstructions() {
        return checkInInstructions;
    }
}

