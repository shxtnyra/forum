package com.shxtnyra.forum.dto.comment;

import com.shxtnyra.forum.dto.post.PostShortDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateDTO {
    @NotBlank
    private final String content;
    @NotBlank
    private final PostShortDTO post;
    private final CommentShortDTO parent;
}
