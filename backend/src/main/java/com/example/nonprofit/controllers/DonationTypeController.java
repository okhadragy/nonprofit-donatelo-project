
package com.example.nonprofit.controllers;

import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.services.DonationTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donation-types")
public class DonationTypeController {

    private final DonationTypeService service;

    public DonationTypeController(DonationTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<DonationType> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DonationType getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationType create(@RequestBody DonationType donationType) {
        return service.create(donationType);
    }

    @PutMapping("/{id}")
    public DonationType update(
            @PathVariable Long id,
            @RequestBody DonationType donationType) {
        return service.update(id, donationType);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
