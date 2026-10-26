package com.example.nonprofit.models.resources;

import jakarta.persistence.*;

@Entity
@Table(name = "beneficiary_categories")
public class BeneficiaryCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public BeneficiaryCategory() {
    }

    public BeneficiaryCategory(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
