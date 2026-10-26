package com.example.nonprofit.models.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BENEFICIARY")
public class Beneficiary extends User {

    private String city;
    private String category;
    private String status;

    public Beneficiary() {}

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

    public Beneficiary(
            String name,
            String email,
            String username,
            String password,
            String city,
            String category,
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
