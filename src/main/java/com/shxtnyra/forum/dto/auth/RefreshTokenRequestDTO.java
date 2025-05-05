package com.shxtnyra.forum.dto.auth;

import lombok.Data;

@Data
public class RefreshTokenRequestDTO {
    private final String refreshToken;
}