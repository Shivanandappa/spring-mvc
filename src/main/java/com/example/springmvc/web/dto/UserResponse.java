package com.example.springmvc.web.dto;

import com.example.springmvc.domain.RoleName;
import com.example.springmvc.domain.User;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        boolean enabled,
        Set<RoleName> roles,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User user) {
        Set<RoleName> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.isEnabled(),
                roles,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
