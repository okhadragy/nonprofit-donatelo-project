package com.example.nonprofit.services;

import com.example.nonprofit.dto.BeneficiaryRequest;
import com.example.nonprofit.dto.BeneficiaryResponse;
import com.example.nonprofit.models.resources.BeneficiaryCategory;
import com.example.nonprofit.models.users.Beneficiary;
import com.example.nonprofit.repositories.BeneficiaryCategoryRepository;
import com.example.nonprofit.repositories.BeneficiaryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final BeneficiaryCategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    public BeneficiaryService(
            BeneficiaryRepository beneficiaryRepository,
            BeneficiaryCategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<BeneficiaryResponse> getAll() {
        return beneficiaryRepository.findAll().stream()
                .map(BeneficiaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public BeneficiaryResponse getById(Long id) {
        Beneficiary beneficiary = findBeneficiaryOrThrow(id);
        return BeneficiaryResponse.fromEntity(beneficiary);
    }

    public BeneficiaryResponse create(BeneficiaryRequest request) {
        validateRequest(request);

        String username = request.getUsername();
        if (username == null || username.isBlank()) {
            username = generateUsername(request.getName());
        }

        if (beneficiaryRepository.existsByUsername(username)) {
            username = username + System.currentTimeMillis() % 1000;
        }

        String email = request.getEmail();
        if (email == null || email.isBlank()) {
            email = username + "@beneficiary.local";
        }

        if (beneficiaryRepository.existsByEmail(email)) {
            email = "beneficiary." + System.currentTimeMillis() + "@beneficiary.local";
        }

        String status = request.getStatus();
        if (status == null || status.isBlank()) {
            status = "Active";
        }

        BeneficiaryCategory category = resolveCategory(request);

        Beneficiary beneficiary = new Beneficiary(
                request.getName(),
                email,
                username,
                passwordEncoder.encode("Beneficiary1234"),
                request.getCity(),
                category,
                status);

        Beneficiary saved = beneficiaryRepository.save(beneficiary);
        return BeneficiaryResponse.fromEntity(saved);
    }

    public BeneficiaryResponse update(Long id, BeneficiaryRequest request) {
        Beneficiary existing = findBeneficiaryOrThrow(id);
        validateRequest(request);

        existing.setName(request.getName());
        existing.setCity(request.getCity());
        existing.setCategory(resolveCategory(request));

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            existing.setStatus(request.getStatus());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            existing.setEmail(request.getEmail());
        }

        Beneficiary saved = beneficiaryRepository.save(existing);
        return BeneficiaryResponse.fromEntity(saved);
    }

    public void delete(Long id) {
        Beneficiary existing = findBeneficiaryOrThrow(id);
        beneficiaryRepository.delete(existing);
    }

    private BeneficiaryCategory resolveCategory(BeneficiaryRequest request) {
        if (request.getCategoryId() != null) {
            return categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Beneficiary category not found with id: " + request.getCategoryId()));
        }
        if (request.getCategory() != null && !request.getCategory().isBlank()) {
            return categoryRepository.findByNameIgnoreCase(request.getCategory().trim())
                    .orElse(null);
        }
        return null;
    }

    private Beneficiary findBeneficiaryOrThrow(Long id) {
        return beneficiaryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Beneficiary not found with id: " + id));
    }

    private void validateRequest(BeneficiaryRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Beneficiary name is required");
        }
        if (request.getCity() == null || request.getCity().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "City is required");
        }
    }

    private String generateUsername(String name) {
        String sanitized = name.toLowerCase().replaceAll("[^a-z0-9]", "");
        if (sanitized.isBlank()) {
            sanitized = "beneficiary";
        }
        return sanitized + (System.currentTimeMillis() % 10000);
    }
}
