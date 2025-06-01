package com.shxtnyra.forum.dto.post;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ContentBlockDTO(
    @NotNull String type,
    boolean cover,
    boolean hidden,
    String anchor,
    @NotNull ContentBlockDataDTO data
) {}