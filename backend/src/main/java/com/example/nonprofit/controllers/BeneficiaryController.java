package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.BeneficiaryRequest;
import com.example.nonprofit.dto.BeneficiaryResponse;
import com.example.nonprofit.services.BeneficiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
public class BeneficiaryController {

    private final BeneficiaryService service;

    public BeneficiaryController(BeneficiaryService service) {
        this.service = service;
    }

    @GetMapping
    public List<BeneficiaryResponse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public BeneficiaryResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeneficiaryResponse create(@RequestBody BeneficiaryRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public BeneficiaryResponse update(
            @PathVariable Long id,
            @RequestBody BeneficiaryRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
