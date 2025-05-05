package com.shxtnyra.forum.dto.topic;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopicDetailsDTO {
    private final Long id;
    private final String name;
    private final String slug;
    private final String previewUrl;
    private final String description;
    private final boolean isPinned;
}
