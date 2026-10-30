package com.example.nonprofit.event_decorator;

public abstract class EventDecorator implements EventComponent {

    protected final EventComponent decoratedEvent;

    public EventDecorator(EventComponent decoratedEvent) {
        if (decoratedEvent == null) {
            throw new IllegalArgumentException("decoratedEvent cannot be null");
        }
        this.decoratedEvent = decoratedEvent;
    }

    @Override
    public String getDescription() {
        return decoratedEvent.getDescription();
    }

    public EventComponent getDecoratedEvent() {
        return decoratedEvent;
    }
}

