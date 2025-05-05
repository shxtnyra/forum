package com.shxtnyra.forum.dto.auth;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private final String accessToken;
    private final String refreshToken;
}
