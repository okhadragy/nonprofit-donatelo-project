package com.example.nonprofit.services;

import com.example.nonprofit.models.resources.BeneficiaryCategory;
import com.example.nonprofit.repositories.BeneficiaryCategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BeneficiaryCategoryService {

    private final BeneficiaryCategoryRepository repository;

    public BeneficiaryCategoryService(BeneficiaryCategoryRepository repository) {
        this.repository = repository;
    }

    public List<BeneficiaryCategory> getAll() {
        return repository.findAll();
    }

    public BeneficiaryCategory getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Beneficiary category not found"));
    }

    public BeneficiaryCategory create(BeneficiaryCategory category) {
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

    public BeneficiaryCategory update(Long id, BeneficiaryCategory updated) {
        BeneficiaryCategory existing = getById(id);

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
        BeneficiaryCategory existing = getById(id);
        repository.delete(existing);
    }
}
