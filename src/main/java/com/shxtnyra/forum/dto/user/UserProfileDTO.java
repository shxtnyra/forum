package com.shxtnyra.forum.dto.user;

import com.shxtnyra.forum.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileDTO {
    private final Long id;
    private final String name;
    private final String nickname;
    private final String profileDescription;
    private final String avatarURL;
    private final LocalDateTime createAt;
    private final LocalDateTime lastActivity;
    private final int rating;
    private final Role role;
}
