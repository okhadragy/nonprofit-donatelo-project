package com.example.nonprofit.config;

import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.repositories.DonationTypeRepository;
import com.example.nonprofit.repositories.UserRepository;
import com.example.nonprofit.models.users.Admin;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            DonationTypeRepository donationTypeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // Seed donation types
            List<String> types = List.of(
                "Cash",
                "Food",
                "Clothing",
                "Medical Supplies",
                "Educational Supplies",
                "Other"
            );

            for (String name : types) {
                if (!donationTypeRepository.existsByNameIgnoreCase(name)) {
                    donationTypeRepository.save(new DonationType(name));
                }
            }

            // Create default admin if one doesn't exist
            String adminUsername = "admin";

            if (!userRepository.existsByUsername(adminUsername)) {
                Admin admin = new Admin(
                    "Default Admin",
                    "a0e8T@example.com",
                    adminUsername,
                    passwordEncoder.encode("Admin1234")
                );
                userRepository.save(admin);

            }

            

            System.out.println("Initial data initialized.");
        };
    }
}