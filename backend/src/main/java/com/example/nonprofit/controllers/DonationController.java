package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.DonationRequest;
import com.example.nonprofit.dto.DonationResponse;
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
    public List<DonationResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DonationResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }
    @GetMapping("/{id}/document")
    public String getDocument(@PathVariable Long id, @RequestParam String type) {
        return service.generateDocument(id, type);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationResponse create(@RequestBody DonationRequest request) {
        DonationStrategyFactory factory = DonationStrategyFactory.getInstance();
        DonationStrategy strategy = factory.getDonationStrategy(request.getMethod());
        if (strategy != null) {
            strategy.processDonation();
        }
        return service.create(request);
    }

    @PutMapping("/{id}")
    public DonationResponse update(
            @PathVariable Long id,
            @RequestBody DonationRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
