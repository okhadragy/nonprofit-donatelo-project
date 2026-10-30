package com.example.nonprofit.dto;

import com.example.nonprofit.models.events.Ticket;

import java.math.BigDecimal;

public class TicketResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Long eventId;
    private String eventTitle;

    public TicketResponse() {
    }

    public static TicketResponse fromEntity(Ticket ticket) {
        TicketResponse dto = new TicketResponse();
        dto.id = ticket.getId();
        dto.name = ticket.getName();
        dto.price = ticket.getPrice();

        if (ticket.getEvent() != null) {
            dto.eventId = ticket.getEvent().getId();
            dto.eventTitle = ticket.getEvent().getTitle();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }
}
