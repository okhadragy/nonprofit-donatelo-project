package com.example.nonprofit.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.nonprofit.models.users.Volunteer;
import com.example.nonprofit.models.VolunteerManagement.Qualification;
import com.example.nonprofit.models.VolunteerManagement.Role;
import com.example.nonprofit.repositories.QualificationRepository;
import com.example.nonprofit.repositories.RoleRepository;
import com.example.nonprofit.repositories.VolunteerRepository;
import com.example.nonprofit.dto.VolunteerRequest;
import com.example.nonprofit.dto.VolunteerResponse;

@Service
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final RoleRepository roleRepository;
    private final QualificationRepository qualificationRepository;

    public VolunteerService(
            VolunteerRepository volunteerRepository,
            RoleRepository roleRepository,
            QualificationRepository qualificationRepository) {
        this.volunteerRepository = volunteerRepository;
        this.roleRepository = roleRepository;
        this.qualificationRepository = qualificationRepository;
    }

    public List<VolunteerResponse> getAllVolunteers() {
        List<VolunteerResponse> responses = new ArrayList<>();
        for (Volunteer volunteer : volunteerRepository.findAll()) {
            responses.add(VolunteerResponse.fromEntity(volunteer));
        }
        return responses;
    }

    public Volunteer findVolunteerOrThrow(Long id) {
        return volunteerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Volunteer not found with id: " + id));
    }

    public VolunteerResponse getVolunteerById(Long id) {
        Volunteer volunteer = findVolunteerOrThrow(id);
        return VolunteerResponse.fromEntity(volunteer);
    }

    public List<VolunteerResponse> filterByRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return getAllVolunteers();
        }
        List<VolunteerResponse> matches = new ArrayList<>();
        for (Volunteer volunteer : volunteerRepository.findAll()) {
            for (Role role : volunteer.getPreferredRoles()) {
                if (roleIds.contains(role.getId())) {
                    matches.add(VolunteerResponse.fromEntity(volunteer));
                    break;
                }
            }
        }
        return matches;
    }

    public List<VolunteerResponse> filterByQualifications(List<Long> qualificationIds) {
        if (qualificationIds == null || qualificationIds.isEmpty()) {
            return getAllVolunteers();
        }
        List<VolunteerResponse> matches = new ArrayList<>();
        for (Volunteer volunteer : volunteerRepository.findAll()) {
            for (Qualification q : volunteer.getQualifications()) {
                if (qualificationIds.contains(q.getId())) {
                    matches.add(VolunteerResponse.fromEntity(volunteer));
                    break;
                }
            }
        }
        return matches;
    }

    public List<VolunteerResponse> filterVolunteers(List<Long> roleIds, List<Long> qualificationIds) {
        boolean hasRoleFilter = roleIds != null && !roleIds.isEmpty();
        boolean hasQualFilter = qualificationIds != null && !qualificationIds.isEmpty();

        if (!hasRoleFilter && !hasQualFilter) {
            return getAllVolunteers();
        }
        if (hasRoleFilter && !hasQualFilter) {
            return filterByRoles(roleIds);
        }
        if (!hasRoleFilter && hasQualFilter) {
            return filterByQualifications(qualificationIds);
        }

        List<VolunteerResponse> matches = new ArrayList<>();
        for (Volunteer volunteer : volunteerRepository.findAll()) {
            boolean matchesRole = false;
            for (Role role : volunteer.getPreferredRoles()) {
                if (roleIds.contains(role.getId())) {
                    matchesRole = true;
                    break;
                }
            }

            boolean matchesQual = false;
            for (Qualification q : volunteer.getQualifications()) {
                if (qualificationIds.contains(q.getId())) {
                    matchesQual = true;
                    break;
                }
            }

            if (matchesRole && matchesQual) {
                matches.add(VolunteerResponse.fromEntity(volunteer));
            }
        }
        return matches;
    }

    public VolunteerResponse assignQualifications(Long id, List<Long> qualificationIds) {
        Volunteer volunteer = findVolunteerOrThrow(id);
        List<Qualification> qualifications = qualificationRepository.findAllById(qualificationIds);
        volunteer.setQualifications(qualifications);
        Volunteer saved = volunteerRepository.save(volunteer);
        return VolunteerResponse.fromEntity(saved);
    }

    public VolunteerResponse assignRoles(Long id, List<Long> roleIds) {
        Volunteer volunteer = findVolunteerOrThrow(id);
        List<Role> roles = roleRepository.findAllById(roleIds);
        volunteer.setPreferredRoles(roles);
        Volunteer saved = volunteerRepository.save(volunteer);
        return VolunteerResponse.fromEntity(saved);
    }

    public VolunteerResponse update(Long id, VolunteerRequest request) {
        validateRequest(request);
        Volunteer volunteer = findVolunteerOrThrow(id);

        volunteer.setName(request.getName());
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            volunteer.setStatus(request.getStatus());
        }
        volunteerRepository.save(volunteer);

        if (request.getQualificationIds() != null) {
            assignQualifications(id, request.getQualificationIds());
        }

        if (request.getPreferredRoleIds() != null) {
            assignRoles(id, request.getPreferredRoleIds());
        }

        return getVolunteerById(id);
    }

    public void deleteVolunteer(Long id) {
        Volunteer volunteer = findVolunteerOrThrow(id);
        volunteerRepository.delete(volunteer);
    }

    private void validateRequest(VolunteerRequest request) {
        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Volunteer request body is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Volunteer name is required");
        }
        if (request.getStatus() != null && request.getStatus().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Volunteer status cannot be blank");
        }
    }
}
