package com.shxtnyra.forum.dto.post;

import com.shxtnyra.forum.dto.user.UserPreviewDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostPreviewDTO {
    private final String title;
    private final LocalDateTime createAt;
    private final UserPreviewDTO author;
}
