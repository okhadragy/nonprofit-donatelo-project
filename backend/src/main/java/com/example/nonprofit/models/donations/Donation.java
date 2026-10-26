package com.example.nonprofit.models.donations;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "donations")
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String method;

    @ManyToOne
    @JoinColumn(name = "donation_type_id")
    private DonationType donationType;

    public Donation() {
    }

    public Donation(BigDecimal amount, Date date, String method,
                    DonationType donationType) {
        this.amount = amount;
        this.date = date;
        this.method = method;
        this.donationType = donationType;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public DonationType getDonationType() {
        return donationType;
    }

    public void setDonationType(DonationType donationType) {
        this.donationType = donationType;
    }

    public String getDescription() {
        return "Donation of " + amount + " via " + method;
    }

    public double getCost() {
        return amount != null ? amount.doubleValue() : 0.0;
    }
}
