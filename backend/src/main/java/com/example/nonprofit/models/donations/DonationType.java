
package com.example.nonprofit.models.donations;

import jakarta.persistence.*;

@Entity
@Table(name = "donation_types")
public class DonationType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public DonationType() {
    }

    public DonationType(String name) {
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
