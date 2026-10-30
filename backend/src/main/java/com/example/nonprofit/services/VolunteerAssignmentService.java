package com.example.nonprofit.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.nonprofit.dto.VolunteerAssignmentRequest;
import com.example.nonprofit.dto.VolunteerAssignmentResponse;
import com.example.nonprofit.models.VolunteerManagement.Qualification;
import com.example.nonprofit.models.VolunteerManagement.Role;
import com.example.nonprofit.models.VolunteerManagement.VolunteerAssignment;
import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.users.Volunteer;
import com.example.nonprofit.repositories.EventRepository;
import com.example.nonprofit.repositories.RoleRepository;
import com.example.nonprofit.repositories.VolunteerAssignmentRepository;
import com.example.nonprofit.repositories.VolunteerRepository;

@Service
public class VolunteerAssignmentService {

    private final VolunteerAssignmentRepository assignmentRepository;
    private final VolunteerRepository volunteerRepository;
    private final EventRepository eventRepository;
    private final RoleRepository roleRepository;

    public VolunteerAssignmentService(
            VolunteerAssignmentRepository assignmentRepository,
            VolunteerRepository volunteerRepository,
            EventRepository eventRepository,
            RoleRepository roleRepository) {
        this.assignmentRepository = assignmentRepository;
        this.volunteerRepository = volunteerRepository;
        this.eventRepository = eventRepository;
        this.roleRepository = roleRepository;
    }

    public VolunteerAssignment findAssignmentOrThrow(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Volunteer assignment not found with id: " + id));
    }

    public List<VolunteerAssignmentResponse> getAssignmentsByEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Event not found with id: " + eventId);
        }

        List<VolunteerAssignmentResponse> responses = new ArrayList<>();
        for (VolunteerAssignment assignment : assignmentRepository.findByEventId(eventId)) {
            responses.add(VolunteerAssignmentResponse.fromEntity(assignment));
        }
        return responses;
    }

    public List<Role> getRolesForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Event not found with id: " + eventId);
        }

        List<Role> roles = new ArrayList<>();
        Set<Long> seenRoleIds = new HashSet<>();

        for (VolunteerAssignment assignment : assignmentRepository.findByEventId(eventId)) {
            Role role = assignment.getRole();
            if (role != null && seenRoleIds.add(role.getId())) {
                roles.add(role);
            }
        }
        return roles;
    }

    public List<Qualification> getQualificationsForEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Event not found with id: " + eventId);
        }

        List<Qualification> qualifications = new ArrayList<>();
        Set<Long> seenQualIds = new HashSet<>();

        for (VolunteerAssignment assignment : assignmentRepository.findByEventId(eventId)) {
            Volunteer volunteer = assignment.getVolunteer();
            if (volunteer != null && volunteer.getQualifications() != null) {
                for (Qualification qual : volunteer.getQualifications()) {
                    if (seenQualIds.add(qual.getId())) {
                        qualifications.add(qual);
                    }
                }
            }
        }
        return qualifications;
    }

    public List<VolunteerAssignmentResponse> getAssignmentsByVolunteer(Long volunteerId) {
        if (!volunteerRepository.existsById(volunteerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Volunteer not found with id: " + volunteerId);
        }

        List<VolunteerAssignmentResponse> responses = new ArrayList<>();
        for (VolunteerAssignment assignment : assignmentRepository.findByVolunteerId(volunteerId)) {
            responses.add(VolunteerAssignmentResponse.fromEntity(assignment));
        }
        return responses;
    }

    public VolunteerAssignmentResponse createAssignment(VolunteerAssignmentRequest request) {
        validateRequest(request);

        Volunteer volunteer = volunteerRepository.findById(request.getVolunteerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Volunteer not found with id: " + request.getVolunteerId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event not found with id: " + request.getEventId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found with id: " + request.getRoleId()));

        VolunteerAssignment assignment = new VolunteerAssignment(
                request.getHoursWorked(),
                volunteer,
                role,
                event
        );

        VolunteerAssignment saved = assignmentRepository.save(assignment);
        return VolunteerAssignmentResponse.fromEntity(saved);
    }

    public VolunteerAssignmentResponse updateAssignment(Long id, VolunteerAssignmentRequest request) {
        validateRequest(request);
        VolunteerAssignment assignment = findAssignmentOrThrow(id);

        Volunteer volunteer = volunteerRepository.findById(request.getVolunteerId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Volunteer not found with id: " + request.getVolunteerId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event not found with id: " + request.getEventId()));

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Role not found with id: " + request.getRoleId()));

        assignment.setHoursWorked(request.getHoursWorked());
        assignment.setVolunteer(volunteer);
        assignment.setEvent(event);
        assignment.setRole(role);

        VolunteerAssignment saved = assignmentRepository.save(assignment);
        return VolunteerAssignmentResponse.fromEntity(saved);
    }

    public void deleteAssignment(Long id) {
        VolunteerAssignment assignment = findAssignmentOrThrow(id);
        assignmentRepository.delete(assignment);
    }

    private void validateRequest(VolunteerAssignmentRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Assignment request body is required");
        }
        if (request.getVolunteerId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Volunteer ID is required");
        }
        if (request.getEventId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Event ID is required");
        }
        if (request.getRoleId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Role ID is required");
        }
        if (request.getHoursWorked() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Hours worked cannot be negative");
        }
    }
}
