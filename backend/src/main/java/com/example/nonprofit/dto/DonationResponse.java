package com.example.nonprofit.dto;

import com.example.nonprofit.models.donations.Donation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class DonationResponse {

    private Long id;
    private BigDecimal amount;
    private String date;
    private String method;
    private Long donorId;
    private String donorName;
    private Long donationTypeId;
    private String donationTypeName;

    public DonationResponse() {
    }

    public static DonationResponse fromEntity(Donation donation) {
        DonationResponse dto = new DonationResponse();
        dto.id = donation.getId();
        dto.amount = donation.getAmount();
        dto.method = donation.getMethod();

        if (donation.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dto.date = sdf.format(donation.getDate());
        }

        if (donation.getDonor() != null) {
            dto.donorId = donation.getDonor().getId();
            dto.donorName = donation.getDonor().getName();
        }

        if (donation.getDonationType() != null) {
            dto.donationTypeId = donation.getDonationType().getId();
            dto.donationTypeName = donation.getDonationType().getName();
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getMethod() {
        return method;
    }

    public Long getDonorId() {
        return donorId;
    }

    public String getDonorName() {
        return donorName;
    }

    public Long getDonationTypeId() {
        return donationTypeId;
    }

    public String getDonationTypeName() {
        return donationTypeName;
    }
}
