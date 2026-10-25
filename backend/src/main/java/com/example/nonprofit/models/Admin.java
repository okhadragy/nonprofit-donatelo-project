package com.example.nonprofit.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    protected Admin() {}

    public Admin(
            String name,
            String email,
            String username,
            String password) {
        super(name, email, username, password, "ADMIN");
    }
}