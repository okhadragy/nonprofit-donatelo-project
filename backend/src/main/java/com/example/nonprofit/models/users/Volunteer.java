package com.example.nonprofit.models.users;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VOLUNTEER")
public class Volunteer extends User {

    private String status;

    protected Volunteer() {}

    public Volunteer(
            String name,
            String email,
            String username,
            String password) {
        super(name, email, username, password, "VOLUNTEER");
        this.status = "PENDING";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}