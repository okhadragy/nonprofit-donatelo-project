package com.example.nonprofit.controllers;

import com.example.nonprofit.models.resources.ResourceCategory;
import com.example.nonprofit.services.ResourceCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resource-categories")
public class ResourceCategoryController {

    private final ResourceCategoryService service;

    public ResourceCategoryController(ResourceCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResourceCategory> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResourceCategory getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceCategory create(@RequestBody ResourceCategory category) {
        return service.create(category);
    }

    @PutMapping("/{id}")
    public ResourceCategory update(
            @PathVariable Long id,
            @RequestBody ResourceCategory category) {
        return service.update(id, category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
