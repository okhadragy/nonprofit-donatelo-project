package com.example.nonprofit.dto;

import java.util.List;

public class DecoratedEventResponse {

    private Long eventId;
    private String eventTitle;
    private String baseDescription;
    private List<String> appliedDecorators;
    private String decoratedDescription;

    public DecoratedEventResponse() {
    }

    public DecoratedEventResponse(
            Long eventId,
            String eventTitle,
            String baseDescription,
            List<String> appliedDecorators,
            String decoratedDescription) {
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.baseDescription = baseDescription;
        this.appliedDecorators = appliedDecorators;
        this.decoratedDescription = decoratedDescription;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getBaseDescription() {
        return baseDescription;
    }

    public void setBaseDescription(String baseDescription) {
        this.baseDescription = baseDescription;
    }

    public List<String> getAppliedDecorators() {
        return appliedDecorators;
    }

    public void setAppliedDecorators(List<String> appliedDecorators) {
        this.appliedDecorators = appliedDecorators;
    }

    public String getDecoratedDescription() {
        return decoratedDescription;
    }

    public void setDecoratedDescription(String decoratedDescription) {
        this.decoratedDescription = decoratedDescription;
    }
}

