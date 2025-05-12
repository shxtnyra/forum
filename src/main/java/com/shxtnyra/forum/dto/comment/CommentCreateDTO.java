package com.shxtnyra.forum.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentCreateDTO {
    @NotBlank
    private final String text;
    @NotNull
    private final Long postId;
    private final Long parentId;
}
