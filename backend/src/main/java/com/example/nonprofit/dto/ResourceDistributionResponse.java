package com.example.nonprofit.dto;

import com.example.nonprofit.models.resources.ResourceDistribution;

import java.text.SimpleDateFormat;

public class ResourceDistributionResponse {

    private Long id;
    private Long resourceId;
    private String resourceName;
    private Long beneficiaryId;
    private String beneficiaryName;
    private int quantity;
    private String distributionDate;

    public ResourceDistributionResponse() {
    }

    public static ResourceDistributionResponse fromEntity(ResourceDistribution dist) {
        ResourceDistributionResponse dto = new ResourceDistributionResponse();
        dto.id = dist.getId();
        dto.quantity = dist.getQuantity();

        if (dist.getResource() != null) {
            dto.resourceId = dist.getResource().getId();
            dto.resourceName = dist.getResource().getName();
        }

        if (dist.getBeneficiary() != null) {
            dto.beneficiaryId = dist.getBeneficiary().getId();
            dto.beneficiaryName = dist.getBeneficiary().getName();
        }

        if (dist.getDistributionDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dto.distributionDate = sdf.format(dist.getDistributionDate());
        }

        return dto;
    }

    public Long getId() {
        return id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Long getBeneficiaryId() {
        return beneficiaryId;
    }

    public String getBeneficiaryName() {
        return beneficiaryName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDistributionDate() {
        return distributionDate;
    }
}
