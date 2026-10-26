package com.example.nonprofit.repositories;

import com.example.nonprofit.models.resources.ResourceDistribution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceDistributionRepository extends JpaRepository<ResourceDistribution, Long> {
    List<ResourceDistribution> findAllByOrderByDistributionDateDesc();
}
