
package com.example.nonprofit.controllers;

import com.example.nonprofit.models.donations.Donation;
import com.example.nonprofit.services.DonationService;
import com.example.nonprofit.donation_strategy.DonationStrategyFactory;
import com.example.nonprofit.donation_strategy.DonationStrategy;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService service;

    public DonationController(DonationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Donation> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Donation getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Donation create(@RequestBody Donation donation) {
        DonationStrategyFactory factory = DonationStrategyFactory.getInstance();
        DonationStrategy strategy = factory.getDonationStrategy(donation.getMethod());
        strategy.processDonation();
        return service.create(donation);
    }

    @PutMapping("/{id}")
    public Donation update(
            @PathVariable Long id,
            @RequestBody Donation donation) {
        return service.update(id, donation);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
