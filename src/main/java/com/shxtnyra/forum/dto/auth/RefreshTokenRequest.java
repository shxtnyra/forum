package com.shxtnyra.forum.dto.auth;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private final String refreshToken;
}