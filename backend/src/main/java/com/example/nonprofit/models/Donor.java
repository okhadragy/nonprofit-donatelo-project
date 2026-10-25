package com.example.nonprofit.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("DONOR")
public class Donor extends User {

    protected Donor() {}

    public Donor(
            String name,
            String email,
            String username,
            String password) {
        super(name, email, username, password, "DONOR");
    }
}