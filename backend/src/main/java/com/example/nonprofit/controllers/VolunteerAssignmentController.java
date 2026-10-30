package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.VolunteerAssignmentRequest;
import com.example.nonprofit.dto.VolunteerAssignmentResponse;
import com.example.nonprofit.models.VolunteerManagement.Qualification;
import com.example.nonprofit.models.VolunteerManagement.Role;
import com.example.nonprofit.services.VolunteerAssignmentService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer-assignments")
public class VolunteerAssignmentController {

    private final VolunteerAssignmentService volunteerAssignmentService;

    public VolunteerAssignmentController(VolunteerAssignmentService volunteerAssignmentService) {
        this.volunteerAssignmentService = volunteerAssignmentService;
    }

    @GetMapping("/event/{eventId}")
    public List<VolunteerAssignmentResponse> getAssignmentsByEvent(@PathVariable Long eventId) {
        return volunteerAssignmentService.getAssignmentsByEvent(eventId);
    }

    @GetMapping("/event/{eventId}/roles")
    public List<Role> getRolesForEvent(@PathVariable Long eventId) {
        return volunteerAssignmentService.getRolesForEvent(eventId);
    }

    @GetMapping("/event/{eventId}/qualifications")
    public List<Qualification> getQualificationsForEvent(@PathVariable Long eventId) {
        return volunteerAssignmentService.getQualificationsForEvent(eventId);
    }

    @GetMapping("/volunteer/{volunteerId}")
    public List<VolunteerAssignmentResponse> getAssignmentsByVolunteer(@PathVariable Long volunteerId) {
        return volunteerAssignmentService.getAssignmentsByVolunteer(volunteerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VolunteerAssignmentResponse create(@RequestBody VolunteerAssignmentRequest request) {
        return volunteerAssignmentService.createAssignment(request);
    }

    @PutMapping("/{id}")
    public VolunteerAssignmentResponse update(
            @PathVariable Long id,
            @RequestBody VolunteerAssignmentRequest request) {
        return volunteerAssignmentService.updateAssignment(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        volunteerAssignmentService.deleteAssignment(id);
    }
}

