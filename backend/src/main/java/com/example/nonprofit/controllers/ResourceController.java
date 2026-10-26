package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.ResourceDistributionResponse;
import com.example.nonprofit.dto.ResourceRequest;
import com.example.nonprofit.dto.ResourceResponse;
import com.example.nonprofit.models.resources.ResourceDistribution;
import com.example.nonprofit.services.ResourceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService service;

    public ResourceController(ResourceService service) {
        this.service = service;
    }

    @GetMapping
    public List<ResourceResponse> getAll() {
        return service.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse create(@RequestBody ResourceRequest request) {
        return service.create(request);
    }

    @PostMapping("/distribute")
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceDistributionResponse distribute(@RequestBody Map<String, Object> body) {
        if (!body.containsKey("resourceId") || !body.containsKey("beneficiaryId")) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Both resourceId and beneficiaryId are required");
        }
        Long resourceId = Long.valueOf(body.get("resourceId").toString());
        Long beneficiaryId = Long.valueOf(body.get("beneficiaryId").toString());
        int quantity = 1;
        if (body.get("quantity") != null) {
            try {
                quantity = Integer.parseInt(body.get("quantity").toString());
            } catch (NumberFormatException ignored) {}
        }
        ResourceDistribution dist = service.distribute(resourceId, beneficiaryId, quantity);
        return ResourceDistributionResponse.fromEntity(dist);
    }

    @GetMapping("/distributions")
    public List<ResourceDistributionResponse> getDistributions() {
        return service.getAllDistributions();
    }
}
