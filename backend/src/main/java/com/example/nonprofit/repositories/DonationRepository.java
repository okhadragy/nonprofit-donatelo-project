
package com.example.nonprofit.repositories;

import com.example.nonprofit.models.donations.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
    
}
