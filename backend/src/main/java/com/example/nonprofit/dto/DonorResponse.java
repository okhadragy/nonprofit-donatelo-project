package com.example.nonprofit.dto;

public class DonorResponse {

    private Long id;
    private String name;

    public DonorResponse() {
    }

    public DonorResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
