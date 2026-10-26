package com.example.nonprofit.repositories;

import com.example.nonprofit.models.resources.BeneficiaryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BeneficiaryCategoryRepository extends JpaRepository<BeneficiaryCategory, Long> {
    Optional<BeneficiaryCategory> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
