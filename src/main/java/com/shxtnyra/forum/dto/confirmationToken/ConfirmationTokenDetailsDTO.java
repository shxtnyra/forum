package com.shxtnyra.forum.dto.confirmationToken;

import com.shxtnyra.forum.dto.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConfirmationTokenDetailsDTO {
    private final Long id;
    private final String token;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiresAt;
    private final LocalDateTime confirmedAt;
    private final UserShortDTO user;
}
