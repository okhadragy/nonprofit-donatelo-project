package com.example.nonprofit.models.events;

import com.example.nonprofit.event_decorator.EventComponent;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "events")
public class Event implements EventComponent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    private String location;

    @Column(nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "event_category_id")
    private EventCategory category;

    public Event() {
    }

    public Event(String title, Date date, String location, String status,
                 EventCategory category) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.status = status;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EventCategory getCategory() {
        return category;
    }

    public void setCategory(EventCategory category) {
        this.category = category;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(title != null ? title : "Event");
        if (date != null) {
            sb.append(" on ").append(date);
        }
        if (location != null && !location.isBlank()) {
            sb.append(" at ").append(location);
        }
        if (status != null && !status.isBlank()) {
            sb.append(" [Status: ").append(status).append("]");
        }
        return sb.toString();
    }
}
