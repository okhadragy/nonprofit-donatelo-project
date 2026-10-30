package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.VolunteerRequest;
import com.example.nonprofit.dto.VolunteerResponse;
import com.example.nonprofit.services.VolunteerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
public class VolunteerController {

    private final VolunteerService volunteerService;

    public VolunteerController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    @GetMapping
    public List<VolunteerResponse> getAll(
            @RequestParam(required = false) List<Long> roleIds,
            @RequestParam(required = false) List<Long> qualificationIds) {
        if ((roleIds != null && !roleIds.isEmpty()) || (qualificationIds != null && !qualificationIds.isEmpty())) {
            return volunteerService.filterVolunteers(roleIds, qualificationIds);
        }
        return volunteerService.getAllVolunteers();
    }

    @GetMapping("/role/{roleId}")
    public List<VolunteerResponse> getByRole(@PathVariable Long roleId) {
        return volunteerService.filterByRoles(List.of(roleId));
    }

    @GetMapping("/qualification/{qualificationId}")
    public List<VolunteerResponse> getByQualification(@PathVariable Long qualificationId) {
        return volunteerService.filterByQualifications(List.of(qualificationId));
    }

    @GetMapping("/{id}")
    public VolunteerResponse getById(@PathVariable Long id) {
        return volunteerService.getVolunteerById(id);
    }

    @PutMapping("/{id}")
    public VolunteerResponse update(
            @PathVariable Long id,
            @RequestBody VolunteerRequest request) {
        return volunteerService.update(id, request);
    }

    @PutMapping("/{id}/qualifications")
    public VolunteerResponse assignQualifications(
            @PathVariable Long id,
            @RequestBody List<Long> qualificationIds) {
        return volunteerService.assignQualifications(id, qualificationIds);
    }

    @PutMapping("/{id}/roles")
    public VolunteerResponse assignRoles(
            @PathVariable Long id,
            @RequestBody List<Long> roleIds) {
        return volunteerService.assignRoles(id, roleIds);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        volunteerService.deleteVolunteer(id);
    }
}

