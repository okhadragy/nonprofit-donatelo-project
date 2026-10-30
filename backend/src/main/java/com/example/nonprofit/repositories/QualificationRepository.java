package com.example.nonprofit.repositories;

import com.example.nonprofit.models.VolunteerManagement.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Long> {
}
