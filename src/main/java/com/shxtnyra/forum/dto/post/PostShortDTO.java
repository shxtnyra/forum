package com.shxtnyra.forum.dto.post;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record PostShortDTO(
    Long id,
    String title,
    String customUri,
    LocalDateTime createdAt,
    UserShortDTO author,
    int viewCount,
    int likeCount
) {}
