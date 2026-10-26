package com.example.nonprofit.repositories;

import com.example.nonprofit.models.donations.DonationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationTypeRepository extends JpaRepository<DonationType, Long> {
    boolean existsByNameIgnoreCase(String name);
}
