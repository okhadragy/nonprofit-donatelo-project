package com.example.nonprofit.repositories;

import com.example.nonprofit.models.events.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
    Optional<EventCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
