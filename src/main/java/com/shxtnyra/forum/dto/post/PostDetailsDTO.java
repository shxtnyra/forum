package com.shxtnyra.forum.dto.post;

import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PostDetailsDTO(
    Long id,
    String title,
    String customUri,
    List<ContentBlockDTO> blocks,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    UserShortDTO author,
    int viewCount,
    int likeCount,
    int dislikeCount,
    boolean draft
) {}

