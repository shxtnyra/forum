package com.shxtnyra.forum.dto.topic;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TopicCreateDTO {
    private final String name;
    private final String slug;
    private final String description;
    private final boolean isPinned;
}
