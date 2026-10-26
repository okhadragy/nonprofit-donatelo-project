package com.example.nonprofit.services;

import com.example.nonprofit.dto.ResourceRequest;
import com.example.nonprofit.dto.ResourceResponse;
import com.example.nonprofit.models.resources.Resource;
import com.example.nonprofit.models.resources.ResourceDistribution;
import com.example.nonprofit.models.users.Beneficiary;
import com.example.nonprofit.repositories.BeneficiaryRepository;
import com.example.nonprofit.repositories.ResourceDistributionRepository;
import com.example.nonprofit.repositories.ResourceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ResourceDistributionRepository distributionRepository;

    public ResourceService(
            ResourceRepository resourceRepository,
            BeneficiaryRepository beneficiaryRepository,
            ResourceDistributionRepository distributionRepository) {
        this.resourceRepository = resourceRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.distributionRepository = distributionRepository;
    }

    public List<ResourceResponse> getAll() {
        return resourceRepository.findAll().stream()
                .map(ResourceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ResourceResponse create(ResourceRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource name is required");
        }
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resource category is required");
        }

        Resource resource = new Resource();
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setCategory(request.getCategory());

        Resource saved = resourceRepository.save(resource);
        return ResourceResponse.fromEntity(saved);
    }

    public void distribute(Long resourceId, Long beneficiaryId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Resource not found with id: " + resourceId));

        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Beneficiary not found with id: " + beneficiaryId));

        ResourceDistribution distribution = new ResourceDistribution(
                beneficiary,
                resource,
                1,
                new Date()
        );

        distributionRepository.save(distribution);
    }
}
