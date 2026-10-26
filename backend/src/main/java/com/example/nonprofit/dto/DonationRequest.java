package com.example.nonprofit.dto;

import java.math.BigDecimal;

public class DonationRequest {

    private BigDecimal amount;
    private String date;
    private String method;
    private Long donorId;
    private Long donationTypeId;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getDonorId() {
        return donorId;
    }

    public void setDonorId(Long donorId) {
        this.donorId = donorId;
    }

    public Long getDonationTypeId() {
        return donationTypeId;
    }

    public void setDonationTypeId(Long donationTypeId) {
        this.donationTypeId = donationTypeId;
    }
}
