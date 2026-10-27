package com.example.nonprofit.services;

import com.example.nonprofit.models.events.EventCategory;
import com.example.nonprofit.repositories.EventCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EventCategoryService {

    private final EventCategoryRepository repository;

    public EventCategoryService(EventCategoryRepository repository) {
        this.repository = repository;
    }

    public List<EventCategory> getAll() {
        return repository.findAll();
    }

    public EventCategory getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Event category not found"));
    }

    public EventCategory create(EventCategory category) {
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

    public EventCategory update(Long id, EventCategory updated) {
        EventCategory existing = getById(id);

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
        EventCategory existing = getById(id);
        repository.delete(existing);
    }
}
