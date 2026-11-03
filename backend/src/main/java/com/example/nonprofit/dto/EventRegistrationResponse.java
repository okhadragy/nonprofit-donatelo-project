package com.example.nonprofit.dto;

import com.example.nonprofit.models.events.EventRegistration;

public class EventRegistrationResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String attendanceStatus;
    private Long eventId;
    private String eventTitle;
    private Long ticketId;
    private String ticketName;

    public EventRegistrationResponse() {
    }

    public static EventRegistrationResponse fromEntity(EventRegistration registration) {
        EventRegistrationResponse dto = new EventRegistrationResponse();
        dto.id = registration.getId();
        dto.attendanceStatus = registration.getAttendanceStatus();

        if (registration.getUser() != null) {
            dto.userId = registration.getUser().getId();
            dto.userName = registration.getUser().getDisplayName();
        }

        if (registration.getEvent() != null) {
            dto.eventId = registration.getEvent().getId();
            dto.eventTitle = registration.getEvent().getTitle();
        }

        if (registration.getTicket() != null) {
            dto.ticketId = registration.getTicket().getId();
            dto.ticketName = registration.getTicket().getName();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public String getTicketName() {
        return ticketName;
    }
}
