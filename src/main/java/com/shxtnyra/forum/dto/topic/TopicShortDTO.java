package com.shxtnyra.forum.dto.topic;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TopicShortDTO {
    private final Long id;
    private final String name;
    private final String slug;
    private final String previewUrl;
}
