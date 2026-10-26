package com.example.nonprofit.services;

import com.example.nonprofit.dto.DonationRequest;
import com.example.nonprofit.dto.DonationResponse;
import com.example.nonprofit.models.donations.Donation;
import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.models.users.User;
import com.example.nonprofit.repositories.DonationRepository;
import com.example.nonprofit.repositories.DonationTypeRepository;
import com.example.nonprofit.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonationTypeRepository donationTypeRepository;
    private final UserRepository userRepository;

    public DonationService(
            DonationRepository donationRepository,
            DonationTypeRepository donationTypeRepository,
            UserRepository userRepository) {
        this.donationRepository = donationRepository;
        this.donationTypeRepository = donationTypeRepository;
        this.userRepository = userRepository;
    }

    public List<DonationResponse> getAll() {
        return donationRepository.findAll().stream()
                .map(DonationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public DonationResponse getById(Long id) {
        Donation donation = findDonationOrThrow(id);
        return DonationResponse.fromEntity(donation);
    }

    public DonationResponse create(DonationRequest request) {
        validateRequest(request);

        Donation donation = new Donation();
        applyRequestToEntity(request, donation);

        Donation saved = donationRepository.save(donation);
        return DonationResponse.fromEntity(saved);
    }

    public DonationResponse update(Long id, DonationRequest request) {
        Donation existing = findDonationOrThrow(id);
        validateRequest(request);
        applyRequestToEntity(request, existing);

        Donation saved = donationRepository.save(existing);
        return DonationResponse.fromEntity(saved);
    }

    public void delete(Long id) {
        Donation existing = findDonationOrThrow(id);
        donationRepository.delete(existing);
    }

    private Donation findDonationOrThrow(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Donation not found"));
    }

    private void validateRequest(DonationRequest request) {
        if (request.getAmount() == null
                || request.getAmount().signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Donation amount must be greater than zero");
        }

        if (request.getDate() == null
                || request.getDate().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Donation date is required");
        }

        if (request.getMethod() == null
                || request.getMethod().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Donation method is required");
        }
    }

    private void applyRequestToEntity(DonationRequest request, Donation donation) {
        donation.setAmount(request.getAmount());
        donation.setMethod(request.getMethod());

        // Parse date string to Date
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date date = sdf.parse(request.getDate());
            donation.setDate(date);
        } catch (ParseException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid date format. Use yyyy-MM-dd");
        }

        // Resolve donor
        if (request.getDonorId() != null) {
            User donor = userRepository.findById(request.getDonorId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Donor not found with id: " + request.getDonorId()));
            donation.setDonor(donor);
        } else {
            donation.setDonor(null);
        }

        // Resolve donation type
        if (request.getDonationTypeId() != null) {
            DonationType donationType = donationTypeRepository
                    .findById(request.getDonationTypeId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Donation type not found with id: " + request.getDonationTypeId()));
            donation.setDonationType(donationType);
        } else {
            donation.setDonationType(null);
        }
    }
}
