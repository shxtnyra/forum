package com.shxtnyra.forum.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostCreateDTO {
    private final String title;
    @NotBlank
    private final String content;
    @NotNull
    private final Long topicId;
}
