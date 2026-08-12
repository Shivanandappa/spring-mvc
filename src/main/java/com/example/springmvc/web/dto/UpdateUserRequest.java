package com.example.springmvc.web.dto;

import com.example.springmvc.domain.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record UpdateUserRequest(
        @NotBlank @Email String email,
        @Size(min = 8, max = 100) String password,
        @NotBlank @Size(max = 100) String firstName,
        @NotBlank @Size(max = 100) String lastName,
        @NotEmpty Set<RoleName> roles,
        Boolean enabled
) {
}
