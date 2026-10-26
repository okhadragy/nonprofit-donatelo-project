package com.example.nonprofit.controllers;

import com.example.nonprofit.models.resources.BeneficiaryCategory;
import com.example.nonprofit.services.BeneficiaryCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiary-categories")
public class BeneficiaryCategoryController {

    private final BeneficiaryCategoryService service;

    public BeneficiaryCategoryController(BeneficiaryCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<BeneficiaryCategory> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BeneficiaryCategory getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryCategory create(@RequestBody BeneficiaryCategory category) {
        return service.create(category);
    }

    @PutMapping("/{id}")
    public BeneficiaryCategory update(
            @PathVariable Long id,
            @RequestBody BeneficiaryCategory category) {
        return service.update(id, category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
