package com.example.nonprofit.dto;

import java.util.ArrayList;
import java.util.List;

public class VolunteerRequest {

    private String name;
    private String status;
    private List<Long> preferredRoleIds;
    private List<Long> qualificationIds;

    public VolunteerRequest() {
    }

    public VolunteerRequest(String name, String status, List<Long> preferredRoleIds, List<Long> qualificationIds) {
        this.name = name;
        this.status = status;
        this.preferredRoleIds = preferredRoleIds;
        this.qualificationIds = qualificationIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Long> getPreferredRoleIds() {
        return preferredRoleIds;
    }

    public void setPreferredRoleIds(List<Long> preferredRoleIds) {
        this.preferredRoleIds = preferredRoleIds;
    }

    public List<Long> getQualificationIds() {
        return qualificationIds;
    }

    public void setQualificationIds(List<Long> qualificationIds) {
        this.qualificationIds = qualificationIds;
    }
}

