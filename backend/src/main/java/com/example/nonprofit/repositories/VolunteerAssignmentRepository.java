package com.example.nonprofit.repositories;

import com.example.nonprofit.models.VolunteerManagement.VolunteerAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerAssignmentRepository extends JpaRepository<VolunteerAssignment, Long> {
    List<VolunteerAssignment> findByEventId(Long eventId);
    List<VolunteerAssignment> findByVolunteerId(Long volunteerId);
}
