package com.shxtnyra.forum.dto.post;

import com.shxtnyra.forum.dto.user.UserPreviewDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostDTO {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;
    private final UserPreviewDTO author;
}
