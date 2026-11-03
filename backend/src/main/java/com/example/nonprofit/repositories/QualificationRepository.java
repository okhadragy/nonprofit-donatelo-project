package com.example.nonprofit.repositories;

import com.example.nonprofit.models.VolunteerManagement.Qualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QualificationRepository extends JpaRepository<Qualification, Long> {
    boolean existsByNameIgnoreCase(String name);
    Optional<Qualification> findByNameIgnoreCase(String name);
}
