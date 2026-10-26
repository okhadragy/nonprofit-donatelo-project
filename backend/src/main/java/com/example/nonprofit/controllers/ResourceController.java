package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.ResourceRequest;
import com.example.nonprofit.dto.ResourceResponse;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void distribute(@RequestBody Map<String, Long> body) {
        Long resourceId = body.get("resourceId");
        Long beneficiaryId = body.get("beneficiaryId");
        service.distribute(resourceId, beneficiaryId);
    }
}
