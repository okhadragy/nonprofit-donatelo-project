package com.example.nonprofit.models.VolunteerManagement;

import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.users.Volunteer;
import jakarta.persistence.*;

@Entity
@Table(name = "volunteer_assignments")
public class VolunteerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private double hoursWorked;

    @ManyToOne
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    public VolunteerAssignment() {
    }

    public VolunteerAssignment(double hoursWorked, Volunteer volunteer, Role role, Event event) {
        this.hoursWorked = hoursWorked;
        this.volunteer = volunteer;
        this.role = role;
        this.event = event;
    }

    public Long getId() {
        return id;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public Volunteer getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(Volunteer volunteer) {
        this.volunteer = volunteer;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}

