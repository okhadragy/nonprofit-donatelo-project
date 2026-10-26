package com.example.nonprofit.repositories;

import com.example.nonprofit.models.users.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    Optional<Beneficiary> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
