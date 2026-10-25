package com.example.nonprofit.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BENEFICIARY")
public class Beneficiary extends User {

    private String city;
    private String category;
    private String status;

    protected Beneficiary() {}

    public Beneficiary(
            String name,
            String email,
            String username,
            String password,
            String city,
            String category) {
        super(name, email, username, password, "BENEFICIARY");
        this.city = city;
        this.category = category;
        this.status = "PENDING";
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