package com.shxtnyra.forum.dto.post;

import jakarta.validation.constraints.*;
import lombok.Builder;
import java.util.List;

@Builder
public record PostCreateDTO(
    @NotBlank String title,
    @NotNull @Size(min = 1) List<ContentBlockDTO> blocks,
    @NotNull Long topicId,
    boolean draft
) {}