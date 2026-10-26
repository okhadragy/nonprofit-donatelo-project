package com.example.nonprofit.config;

import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.repositories.DonationTypeRepository;
import com.example.nonprofit.repositories.UserRepository;
import com.example.nonprofit.models.users.Admin;
import com.example.nonprofit.models.users.Donor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

            // Seed sample donors
            List<String[]> donors = List.of(
                new String[]{"Ahmed Hassan", "ahmed@example.com", "ahmed"},
                new String[]{"Sara Ali", "sara@example.com", "sara"},
                new String[]{"Mona Khalil", "mona@example.com", "mona"}
            );

            for (String[] d : donors) {
                if (!userRepository.existsByUsername(d[2])) {
                    Donor donor = new Donor(
                        d[0], d[1], d[2],
                        passwordEncoder.encode("Donor1234")
                    );
                    userRepository.save(donor);
                }
            }

            

            System.out.println("Initial data initialized.");
        };
    }
}
