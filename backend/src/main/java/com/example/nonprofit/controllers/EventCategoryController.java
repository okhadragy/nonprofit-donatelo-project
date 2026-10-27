package com.example.nonprofit.controllers;

import com.example.nonprofit.models.events.EventCategory;
import com.example.nonprofit.services.EventCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/event-categories")
public class EventCategoryController {

    private final EventCategoryService service;

    public EventCategoryController(EventCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventCategory> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public EventCategory getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventCategory create(@RequestBody EventCategory category) {
        return service.create(category);
    }

    @PutMapping("/{id}")
    public EventCategory update(
            @PathVariable Long id,
            @RequestBody EventCategory category) {
        return service.update(id, category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
