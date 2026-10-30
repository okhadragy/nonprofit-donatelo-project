package com.example.nonprofit.dto;

import com.example.nonprofit.models.VolunteerManagement.Qualification;
import com.example.nonprofit.models.VolunteerManagement.Role;
import com.example.nonprofit.models.users.Volunteer;

import java.util.ArrayList;
import java.util.List;

public class VolunteerResponse {

    private Long id;
    private String name;
    private String email;
    private String username;
    private String status;

    private List<Long> preferredRoleIds = new ArrayList<>();
    private List<String> preferredRoles = new ArrayList<>();

    private List<Long> qualificationIds = new ArrayList<>();
    private List<String> qualifications = new ArrayList<>();

    public VolunteerResponse() {
    }

    public static VolunteerResponse fromEntity(Volunteer volunteer) {
        VolunteerResponse dto = new VolunteerResponse();
        dto.id = volunteer.getId();
        dto.name = volunteer.getName();
        dto.email = volunteer.getEmail();
        dto.username = volunteer.getUsername();
        dto.status = volunteer.getStatus();

        if (volunteer.getPreferredRoles() != null) {
            for (Role role : volunteer.getPreferredRoles()) {
                dto.preferredRoleIds.add(role.getId());
                dto.preferredRoles.add(role.getName());
            }
        }

        if (volunteer.getQualifications() != null) {
            for (Qualification qualification : volunteer.getQualifications()) {
                dto.qualificationIds.add(qualification.getId());
                dto.qualifications.add(qualification.getName());
            }
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

    public List<Long> getPreferredRoleIds() {
        return preferredRoleIds;
    }

    public List<String> getPreferredRoles() {
        return preferredRoles;
    }

    public List<Long> getQualificationIds() {
        return qualificationIds;
    }

    public List<String> getQualifications() {
        return qualifications;
    }
}
