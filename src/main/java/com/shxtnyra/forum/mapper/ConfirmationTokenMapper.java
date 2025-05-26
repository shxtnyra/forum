package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.confirmationToken.ConfirmationTokenDetailsDTO;
import com.shxtnyra.forum.entity.ConfirmationTokenEntity;

public class ConfirmationTokenMapper {
    public static ConfirmationTokenDetailsDTO toDetailsDTO(ConfirmationTokenEntity confirmationToken) {
        return ConfirmationTokenDetailsDTO.builder()
                .id(confirmationToken.getId())
                .token(confirmationToken.getToken())
                .createdAt(confirmationToken.getCreatedAt())
                .expiresAt(confirmationToken.getExpiresAt())
                .confirmedAt(confirmationToken.getConfirmedAt())
                .user(UserMapper.toShortDTO(confirmationToken.getUser()))
                .build();
    }
}
