package com.example.nonprofit.config;

import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.models.resources.Resource;
import com.example.nonprofit.models.resources.ResourceDistribution;
import com.example.nonprofit.models.users.Admin;
import com.example.nonprofit.models.users.Beneficiary;
import com.example.nonprofit.models.users.Donor;
import com.example.nonprofit.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            DonationTypeRepository donationTypeRepository,
            UserRepository userRepository,
            BeneficiaryRepository beneficiaryRepository,
            ResourceRepository resourceRepository,
            ResourceDistributionRepository distributionRepository,
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
                    "admin@example.com",
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

            // Seed sample beneficiaries
            List<String[]> beneficiariesData = List.of(
                new String[]{"Hadir family", "hadir@example.com", "hadir_fam", "Giza", "Family", "Active"},
                new String[]{"Omar Fathy", "omar.fathy@example.com", "omarfathy", "Cairo", "Student", "Active"},
                new String[]{"Mrs. Salma", "salma@example.com", "salma_elder", "Alexandria", "Elder", "Waitlist"},
                new String[]{"Ali & Mona", "alimona@example.com", "alimona", "Cairo", "Orphan", "Active"}
            );

            for (String[] b : beneficiariesData) {
                if (!beneficiaryRepository.existsByUsername(b[2])) {
                    Beneficiary beneficiary = new Beneficiary(
                        b[0], b[1], b[2],
                        passwordEncoder.encode("Beneficiary1234"),
                        b[3], b[4], b[5]
                    );
                    beneficiaryRepository.save(beneficiary);
                }
            }

            // Seed sample resources
            if (resourceRepository.count() == 0) {
                resourceRepository.save(new Resource("Emergency Food Box", "Monthly staples box with rice, oil, sugar and canned food", "Food"));
                resourceRepository.save(new Resource("Winter Jacket & Blanket", "Thermal adult jacket and heavy wool blanket", "Clothing"));
                resourceRepository.save(new Resource("Student Backpack & Stationery", "Standard school backpack with notebooks and pencil kit", "Education"));
                resourceRepository.save(new Resource("First Aid & Medicine Kit", "Basic first aid kit with pain relief and antiseptic supplies", "Health"));
            }

            // Seed sample distribution
            if (distributionRepository.count() == 0) {
                beneficiaryRepository.findByUsername("hadir_fam").ifPresent(ben -> {
                    resourceRepository.findAll().stream().findFirst().ifPresent(res -> {
                        distributionRepository.save(new ResourceDistribution(ben, res, 1, new Date()));
                    });
                });
            }

            System.out.println("Initial data initialized successfully.");
        };
    }
}
