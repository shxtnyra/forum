package com.shxtnyra.forum.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDTO {
    @NotBlank
    private final String loginOrEmail; // Поле может содержать и логин, и email
    @NotBlank
    private final String password;
}
