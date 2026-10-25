package com.example.nonprofit.controllers;

import java.time.Instant;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StatusController {

    // GET http://localhost:8080/api/status
    @GetMapping("/status")
    public Map<String, String> status() {
        return Map.of(
                "status", "UP",
                "message", "Spring Boot backend is running",
                "time", Instant.now().toString());
    }
}
