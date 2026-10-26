package com.example.nonprofit.repositories;

import com.example.nonprofit.models.resources.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResourceCategoryRepository extends JpaRepository<ResourceCategory, Long> {
    Optional<ResourceCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
