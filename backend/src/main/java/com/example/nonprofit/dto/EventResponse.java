package com.example.nonprofit.dto;

import com.example.nonprofit.models.events.Event;

import java.text.SimpleDateFormat;

public class EventResponse {

    private Long id;
    private String title;
    private String date;
    private String location;
    private String status;
    private Long categoryId;
    private String categoryName;
    private EventDecoratorSettings decorators;

    public EventResponse() {
    }

    public static EventResponse fromEntity(Event event) {
        EventResponse dto = new EventResponse();
        dto.id = event.getId();
        dto.title = event.getTitle();
        dto.location = event.getLocation();
        dto.status = event.getStatus();

        if (event.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dto.date = sdf.format(event.getDate());
        }

        if (event.getCategory() != null) {
            dto.categoryId = event.getCategory().getId();
            dto.categoryName = event.getCategory().getName();
        }

        dto.decorators = new EventDecoratorSettings(
                event.isDecoratorFundraising(),
                event.isDecoratorReminder(),
                event.isDecoratorFollowUp(),
                event.getDecoratorFundraisingGoal());
        return dto;
    }

    public EventDecoratorSettings getDecorators() {
        return decorators;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
