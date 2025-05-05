package com.shxtnyra.forum.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
public class RegisterRequestDTO {
    @NotBlank
    @Size(max = 32, min = 4)
    private final String username;

    @Email
    @NotBlank
    private final String email;

    @NotBlank
    @Size(min = 8, max = 32)
    private final String password;
}
