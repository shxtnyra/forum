package com.shxtnyra.forum.dto.comment;

import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.dto.user.UserShortDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentDetailsDTO {
    private final Long id;
    private final String content;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final UserShortDTO author;
    private final PostShortDTO post;
    private final CommentShortDTO parent;
}
