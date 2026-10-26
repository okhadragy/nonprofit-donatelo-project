package com.example.nonprofit.models.users;

import com.example.nonprofit.models.resources.BeneficiaryCategory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@DiscriminatorValue("BENEFICIARY")
public class Beneficiary extends User {

    private String city;
    private String status;

    @ManyToOne
    @JoinColumn(name = "beneficiary_category_id")
    private BeneficiaryCategory category;

    public Beneficiary() {}

    public Beneficiary(
            String name,
            String email,
            String username,
            String password,
            String city,
            BeneficiaryCategory category) {
        super(name, email, username, password, "BENEFICIARY");
        this.city = city;
        this.category = category;
        this.status = "PENDING";
    }

    public Beneficiary(
            String name,
            String email,
            String username,
            String password,
            String city,
            BeneficiaryCategory category,
            String status) {
        super(name, email, username, password, "BENEFICIARY");
        this.city = city;
        this.category = category;
        this.status = status != null ? status : "Active";
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public BeneficiaryCategory getCategory() {
        return category;
    }

    public void setCategory(BeneficiaryCategory category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
