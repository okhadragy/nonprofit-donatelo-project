
package com.example.nonprofit.services;

import com.example.nonprofit.models.donations.Donation;
import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.repositories.DonationRepository;
import com.example.nonprofit.repositories.DonationTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonationTypeRepository donationTypeRepository;

    public DonationService(
            DonationRepository donationRepository,
            DonationTypeRepository donationTypeRepository) {
        this.donationRepository = donationRepository;
        this.donationTypeRepository = donationTypeRepository;
    }

    public List<Donation> getAll() {
        return donationRepository.findAll();
    }

    public Donation getById(Long id) {
        return donationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Donation not found"));
    }

    public Donation create(Donation donation) {
        validateDonation(donation);
        resolveDonationType(donation);
        return donationRepository.save(donation);
    }

    public Donation update(Long id, Donation updated) {
        Donation existing = getById(id);

        validateDonation(updated);
        resolveDonationType(updated);

        existing.setAmount(updated.getAmount());
        existing.setDate(updated.getDate());
        existing.setMethod(updated.getMethod());
        existing.setDonationType(updated.getDonationType());

        return donationRepository.save(existing);
    }

    public void delete(Long id) {
        Donation existing = getById(id);
        donationRepository.delete(existing);
    }

    private void validateDonation(Donation donation) {
        if (donation.getAmount() == null
                || donation.getAmount().signum() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Donation amount must be greater than zero");
        }

        if (donation.getDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Donation date is required");
        }

        if (donation.getMethod() == null
                || donation.getMethod().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Donation method is required");
        }
    }

    private void resolveDonationType(Donation donation) {
        DonationType suppliedType = donation.getDonationType();

        if (suppliedType == null) {
            return; // Donation type is optional in your current model.
        }

        if (suppliedType.getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide an existing donation type ID");
        }

        DonationType managedType = donationTypeRepository
                .findById(suppliedType.getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Donation type not found"));

        donation.setDonationType(managedType);
    }
}
