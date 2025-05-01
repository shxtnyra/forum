package com.shxtnyra.forum.dto.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPreviewDTO {
    private final Long id;
    private final String nickname;
    private final String avatarUrl;
    private final int rating;
}
