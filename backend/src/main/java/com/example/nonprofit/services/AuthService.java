package com.example.nonprofit.services;

import com.example.nonprofit.dto.AuthResponse;
import com.example.nonprofit.dto.LoginRequest;
import com.example.nonprofit.dto.RegisterRequest;
import com.example.nonprofit.models.users.Donor;
import com.example.nonprofit.models.users.User;
import com.example.nonprofit.models.users.Volunteer;
import com.example.nonprofit.repositories.UserRepository;
import com.example.nonprofit.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Email already exists");
        }

        User user;
        if ("VOLUNTEER".equalsIgnoreCase(request.getRole())) {
            user = new Volunteer(
                    request.getName(),
                    request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()));
        } else {
            user = new Donor(
                    request.getName(),
                    request.getEmail(),
                    request.getUsername(),
                    passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);

        UserDetails principal =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRole())
                        .build();

        String token = jwtService.generateToken(principal);

        return new AuthResponse(token, "Bearer");
    }

    public AuthResponse login(LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()));

        UserDetails principal =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(principal);

        return new AuthResponse(token, "Bearer");
    }
}

