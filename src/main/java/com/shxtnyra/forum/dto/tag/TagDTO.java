package com.shxtnyra.forum.dto.tag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TagDTO {
    private final Long id;
    private final String name;
    private final String description;
} 