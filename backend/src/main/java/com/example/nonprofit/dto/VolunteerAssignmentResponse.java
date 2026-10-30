package com.example.nonprofit.dto;

import com.example.nonprofit.models.VolunteerManagement.VolunteerAssignment;

public class VolunteerAssignmentResponse {
    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private Long eventId;
    private String eventTitle;
    private Long roleId;
    private String roleName;
    private double hoursWorked;

    public VolunteerAssignmentResponse() {
    }

    public static VolunteerAssignmentResponse fromEntity(VolunteerAssignment assignment) {
        VolunteerAssignmentResponse dto = new VolunteerAssignmentResponse();
        dto.id = assignment.getId();
        dto.hoursWorked = assignment.getHoursWorked();

        if (assignment.getVolunteer() != null) {
            dto.volunteerId = assignment.getVolunteer().getId();
            dto.volunteerName = assignment.getVolunteer().getName();
        }

        if (assignment.getEvent() != null) {
            dto.eventId = assignment.getEvent().getId();
            dto.eventTitle = assignment.getEvent().getTitle();
        }

        if (assignment.getRole() != null) {
            dto.roleId = assignment.getRole().getId();
            dto.roleName = assignment.getRole().getName();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getVolunteerId() {
        return volunteerId;
    }

    public String getVolunteerName() {
        return volunteerName;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }
}

