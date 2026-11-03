package com.example.nonprofit.dto;

import com.example.nonprofit.models.users.User;

/**
 * Public view of a user (never includes the password).
 */
public class UserResponse {

    private Long id;
    private String name;
    private String username;
    private String email;
    private String role;

    public UserResponse() {
    }

    public static UserResponse fromEntity(User user) {
        UserResponse dto = new UserResponse();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.role = user.getRole();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
