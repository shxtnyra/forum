package com.shxtnyra.forum.dto.comment;

import com.shxtnyra.forum.dto.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentShortDTO {
    private final Long id;
    private final LocalDateTime createdAt;
    private final UserShortDTO author;
}
