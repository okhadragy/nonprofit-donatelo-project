package com.example.nonprofit.dto;

import com.example.nonprofit.models.resources.Resource;

public class ResourceResponse {

    private Long id;
    private String name;
    private String description;
    private String category;

    public ResourceResponse() {
    }

    public static ResourceResponse fromEntity(Resource resource) {
        ResourceResponse dto = new ResourceResponse();
        dto.id = resource.getId();
        dto.name = resource.getName();
        dto.description = resource.getDescription();
        dto.category = resource.getCategory();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }
}
