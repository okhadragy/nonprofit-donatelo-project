package com.example.nonprofit.models.resources;

import com.example.nonprofit.models.users.Beneficiary;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "resource_distributions")
public class ResourceDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int quantity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date distributionDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "beneficiary_id")
    private Beneficiary beneficiary;

    @ManyToOne(optional = false)
    @JoinColumn(name = "resource_id")
    private Resource resource;

    public ResourceDistribution() {
    }

    public ResourceDistribution(Beneficiary beneficiary, Resource resource, int quantity, Date distributionDate) {
        this.beneficiary = beneficiary;
        this.resource = resource;
        this.quantity = quantity;
        this.distributionDate = distributionDate;
    }

    public Long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getDistributionDate() {
        return distributionDate;
    }

    public void setDistributionDate(Date distributionDate) {
        this.distributionDate = distributionDate;
    }

    public Beneficiary getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(Beneficiary beneficiary) {
        this.beneficiary = beneficiary;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }
}
