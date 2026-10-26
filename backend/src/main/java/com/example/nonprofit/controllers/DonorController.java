package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.DonorResponse;
import com.example.nonprofit.repositories.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    private final UserRepository userRepository;

    public DonorController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<DonorResponse> getAll() {
        return userRepository.findAll().stream()
                .filter(user -> "DONOR".equals(user.getRole()))
                .map(user -> new DonorResponse(user.getId(), user.getName()))
                .collect(Collectors.toList());
    }
}
