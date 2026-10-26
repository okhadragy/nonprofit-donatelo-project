package com.example.nonprofit.services;

import com.example.nonprofit.dto.ResourceDistributionResponse;
import com.example.nonprofit.dto.ResourceRequest;
import com.example.nonprofit.dto.ResourceResponse;
import com.example.nonprofit.models.resources.Resource;
import com.example.nonprofit.models.resources.ResourceCategory;
import com.example.nonprofit.models.resources.ResourceDistribution;
import com.example.nonprofit.models.users.Beneficiary;
import com.example.nonprofit.repositories.BeneficiaryRepository;
import com.example.nonprofit.repositories.ResourceCategoryRepository;
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
    private final ResourceCategoryRepository categoryRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ResourceDistributionRepository distributionRepository;

    public ResourceService(
            ResourceRepository resourceRepository,
            ResourceCategoryRepository categoryRepository,
            BeneficiaryRepository beneficiaryRepository,
            ResourceDistributionRepository distributionRepository) {
        this.resourceRepository = resourceRepository;
        this.categoryRepository = categoryRepository;
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

        ResourceCategory category = resolveCategory(request);
        if (category == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valid resource category is required");
        }

        Resource resource = new Resource();
        resource.setName(request.getName());
        resource.setDescription(request.getDescription());
        resource.setCategory(category);

        Resource saved = resourceRepository.save(resource);
        return ResourceResponse.fromEntity(saved);
    }

    public ResourceDistribution distribute(Long resourceId, Long beneficiaryId, int quantity) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Resource not found with id: " + resourceId));

        Beneficiary beneficiary = beneficiaryRepository.findById(beneficiaryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Beneficiary not found with id: " + beneficiaryId));

        ResourceDistribution distribution = new ResourceDistribution(
                beneficiary,
                resource,
                quantity > 0 ? quantity : 1,
                new Date()
        );

        return distributionRepository.save(distribution);
    }

    public void distribute(Long resourceId, Long beneficiaryId) {
        distribute(resourceId, beneficiaryId, 1);
    }

    public List<ResourceDistributionResponse> getAllDistributions() {
        return distributionRepository.findAllByOrderByDistributionDateDesc().stream()
                .map(ResourceDistributionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    private ResourceCategory resolveCategory(ResourceRequest request) {
        if (request.getCategoryId() != null) {
            return categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Resource category not found with id: " + request.getCategoryId()));
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            return categoryRepository.findByNameIgnoreCase(request.getCategory().trim())
                    .orElse(null);
        }
        return null;
    }
}
