package com.example.nonprofit.services;

import com.example.nonprofit.models.resources.ResourceCategory;
import com.example.nonprofit.repositories.ResourceCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ResourceCategoryService {

    private final ResourceCategoryRepository repository;

    public ResourceCategoryService(ResourceCategoryRepository repository) {
        this.repository = repository;
    }

    public List<ResourceCategory> getAll() {
        return repository.findAll();
    }

    public ResourceCategory getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Resource category not found"));
    }

    public ResourceCategory create(ResourceCategory category) {
        if (category.getName() == null || category.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name is required");
        }

        category.setName(category.getName().trim());

        if (repository.existsByNameIgnoreCase(category.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Category already exists");
        }

        return repository.save(category);
    }

    public ResourceCategory update(Long id, ResourceCategory updated) {
        ResourceCategory existing = getById(id);

        if (updated.getName() == null || updated.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name is required");
        }

        String name = updated.getName().trim();
        boolean duplicate = repository.existsByNameIgnoreCase(name)
                && !existing.getName().equalsIgnoreCase(name);

        if (duplicate) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Category already exists");
        }

        existing.setName(name);
        return repository.save(existing);
    }

    public void delete(Long id) {
        ResourceCategory existing = getById(id);
        repository.delete(existing);
    }
}
