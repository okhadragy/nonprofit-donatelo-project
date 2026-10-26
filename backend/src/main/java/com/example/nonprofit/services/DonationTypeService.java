
package com.example.nonprofit.services;

import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.repositories.DonationTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DonationTypeService {

    private final DonationTypeRepository repository;

    public DonationTypeService(DonationTypeRepository repository) {
        this.repository = repository;
    }

    public List<DonationType> getAll() {
        return repository.findAll();
    }

    public DonationType getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Donation type not found"));
    }

    public DonationType create(DonationType donationType) {
        if (donationType.getName() == null
                || donationType.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name is required");
        }

        donationType.setName(donationType.getName().trim());

        if (repository.existsByNameIgnoreCase(donationType.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Donation type already exists");
        }

        return repository.save(donationType);
    }

    public DonationType update(Long id, DonationType updated) {
        DonationType existing = getById(id);

        if (updated.getName() == null || updated.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Name is required");
        }

        String name = updated.getName().trim();

        boolean duplicate = repository.existsByNameIgnoreCase(name)
                && !existing.getName().equalsIgnoreCase(name);

        if (duplicate) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Donation type already exists");
        }

        existing.setName(name);
        return repository.save(existing);
    }

    public void delete(Long id) {
        DonationType existing = getById(id);
        repository.delete(existing);
    }
}
