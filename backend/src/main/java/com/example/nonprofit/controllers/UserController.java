package com.example.nonprofit.controllers;

import com.example.nonprofit.dto.UserResponse;
import com.example.nonprofit.repositories.UserRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Lists every user in the database (used by the registration attendee drop-down). */
    @GetMapping
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(UserResponse::fromEntity)
                .sorted(Comparator.comparing(
                        (UserResponse u) -> u.getName() == null ? "" : u.getName().toLowerCase()))
                .collect(Collectors.toList());
    }
}
