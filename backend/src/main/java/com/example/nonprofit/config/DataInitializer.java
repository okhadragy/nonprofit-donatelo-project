package com.example.nonprofit.config;

import com.example.nonprofit.models.donations.DonationType;
import com.example.nonprofit.models.events.Event;
import com.example.nonprofit.models.events.EventCategory;
import com.example.nonprofit.models.resources.BeneficiaryCategory;
import com.example.nonprofit.models.resources.Resource;
import com.example.nonprofit.models.resources.ResourceCategory;
import com.example.nonprofit.models.resources.ResourceDistribution;
import com.example.nonprofit.models.users.Admin;
import com.example.nonprofit.models.users.Beneficiary;
import com.example.nonprofit.models.users.Donor;
import com.example.nonprofit.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeData(
            DonationTypeRepository donationTypeRepository,
            UserRepository userRepository,
            BeneficiaryRepository beneficiaryRepository,
            BeneficiaryCategoryRepository beneficiaryCategoryRepository,
            ResourceRepository resourceRepository,
            ResourceCategoryRepository resourceCategoryRepository,
            ResourceDistributionRepository distributionRepository,
            EventCategoryRepository eventCategoryRepository,
            EventRepository eventRepository,
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

            // Seed beneficiary categories
            List<String> benCats = List.of("Family", "Student", "Elder", "Orphan", "Medical Need");
            for (String catName : benCats) {
                if (!beneficiaryCategoryRepository.existsByNameIgnoreCase(catName)) {
                    beneficiaryCategoryRepository.save(new BeneficiaryCategory(catName));
                }
            }

            // Seed resource categories
            List<String> resCats = List.of("Food", "Clothing", "Education", "Health", "General");
            for (String catName : resCats) {
                if (!resourceCategoryRepository.existsByNameIgnoreCase(catName)) {
                    resourceCategoryRepository.save(new ResourceCategory(catName));
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
                    BeneficiaryCategory category = beneficiaryCategoryRepository.findByNameIgnoreCase(b[4]).orElse(null);
                    Beneficiary beneficiary = new Beneficiary(
                        b[0], b[1], b[2],
                        passwordEncoder.encode("Beneficiary1234"),
                        b[3], category, b[5]
                    );
                    beneficiaryRepository.save(beneficiary);
                }
            }

            // Seed sample resources
            if (resourceRepository.count() == 0) {
                ResourceCategory foodCat = resourceCategoryRepository.findByNameIgnoreCase("Food").orElse(null);
                ResourceCategory clothCat = resourceCategoryRepository.findByNameIgnoreCase("Clothing").orElse(null);
                ResourceCategory eduCat = resourceCategoryRepository.findByNameIgnoreCase("Education").orElse(null);
                ResourceCategory healthCat = resourceCategoryRepository.findByNameIgnoreCase("Health").orElse(null);

                resourceRepository.save(new Resource("Emergency Food Box", "Monthly staples box with rice, oil, sugar and canned food", foodCat));
                resourceRepository.save(new Resource("Winter Jacket & Blanket", "Thermal adult jacket and heavy wool blanket", clothCat));
                resourceRepository.save(new Resource("Student Backpack & Stationery", "Standard school backpack with notebooks and pencil kit", eduCat));
                resourceRepository.save(new Resource("First Aid & Medicine Kit", "Basic first aid kit with pain relief and antiseptic supplies", healthCat));
            }

            // Seed sample distribution
            if (distributionRepository.count() == 0) {
                beneficiaryRepository.findByUsername("hadir_fam").ifPresent(ben -> {
                    resourceRepository.findAll().stream().findFirst().ifPresent(res -> {
                        distributionRepository.save(new ResourceDistribution(ben, res, 2, new Date()));
                    });
                });
            }

            // Seed event categories matching the project requirements:
            // "Create, manage, and track events (e.g., fundraisers, workshops, outreach programs)."
            List<String> eventCats = List.of("Fundraiser", "Workshop", "Outreach Program", "Other");
            for (String ecName : eventCats) {
                if (!eventCategoryRepository.existsByNameIgnoreCase(ecName)) {
                    eventCategoryRepository.save(new EventCategory(ecName));
                }
            }

            // Seed sample events
            if (eventRepository.count() == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                EventCategory fundraiserCat = eventCategoryRepository.findByNameIgnoreCase("Fundraiser").orElse(null);
                EventCategory workshopCat = eventCategoryRepository.findByNameIgnoreCase("Workshop").orElse(null);
                EventCategory outreachCat = eventCategoryRepository.findByNameIgnoreCase("Outreach Program").orElse(null);

                Event annualFundraiser = new Event(
                    "Annual Charity Gala & Fundraiser 2026",
                    sdf.parse("2026-11-15"),
                    "Grand Nile Tower, Cairo",
                    "Planned",
                    fundraiserCat
                );
                eventRepository.save(annualFundraiser);

                Event foodDrive = new Event(
                    "Winter Warmth Outreach Program",
                    sdf.parse("2026-11-05"),
                    "Giza Community Center",
                    "Ongoing",
                    outreachCat
                );
                eventRepository.save(foodDrive);

                Event workshop = new Event(
                    "Youth Coding & Mentorship Workshop",
                    sdf.parse("2026-11-20"),
                    "Alexandria Public Library",
                    "Planned",
                    workshopCat
                );
                eventRepository.save(workshop);

                Event cleanWater = new Event(
                    "Clean Water Initiative Fundraiser",
                    sdf.parse("2026-10-10"),
                    "Downtown Cultural Center, Cairo",
                    "Completed",
                    fundraiserCat
                );
                eventRepository.save(cleanWater);
            }

            System.out.println("Initial data initialized successfully.");
        };
    }
}
