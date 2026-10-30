package com.example.nonprofit.repositories;

import com.example.nonprofit.models.users.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {
    Optional<Volunteer> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

