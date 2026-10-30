package com.example.nonprofit.dto;

public class VolunteerAssignmentRequest {

    private Long volunteerId;
    private Long eventId;
    private Long roleId;
    private double hoursWorked;

    public VolunteerAssignmentRequest() {
    }

    public VolunteerAssignmentRequest(Long volunteerId, Long eventId, Long roleId, double hoursWorked) {
        this.volunteerId = volunteerId;
        this.eventId = eventId;
        this.roleId = roleId;
        this.hoursWorked = hoursWorked;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public void setVolunteerId(Long volunteerId) {
        this.volunteerId = volunteerId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
}
