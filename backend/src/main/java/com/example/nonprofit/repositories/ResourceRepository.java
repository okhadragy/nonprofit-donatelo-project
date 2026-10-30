package com.example.nonprofit.repositories;

import com.example.nonprofit.models.resources.Resource;
import com.example.nonprofit.models.resources.ResourceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByCategory(ResourceCategory category);
    List<Resource> findByCategory_NameIgnoreCase(String categoryName);
}
