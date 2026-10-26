package com.example.nonprofit.dto;

import com.example.nonprofit.models.users.Beneficiary;

public class BeneficiaryResponse {

    private Long id;
    private String name;
    private String email;
    private String username;
    private String city;
    private String category;
    private String status;

    public BeneficiaryResponse() {
    }

    public static BeneficiaryResponse fromEntity(Beneficiary beneficiary) {
        BeneficiaryResponse dto = new BeneficiaryResponse();
        dto.id = beneficiary.getId();
        dto.name = beneficiary.getName();
        dto.email = beneficiary.getEmail();
        dto.username = beneficiary.getUsername();
        dto.city = beneficiary.getCity();
        dto.category = beneficiary.getCategory();
        dto.status = beneficiary.getStatus();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getCity() {
        return city;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }
}
