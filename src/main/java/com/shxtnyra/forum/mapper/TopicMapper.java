package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.topic.TopicDetailsDTO;
import com.shxtnyra.forum.dto.topic.TopicShortDTO;
import com.shxtnyra.forum.entity.TopicEntity;

public class TopicMapper {
    public static TopicDetailsDTO toDetailsDTO(TopicEntity entity){
        return TopicDetailsDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .slug(entity.getSlug())
                .previewUrl(entity.getPreviewUrl())
                .isPinned(entity.isPinned())
                .build();
    }

    public static TopicShortDTO toShortDTO(TopicEntity entity){
        return TopicShortDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .previewUrl(entity.getPreviewUrl())
                .build();
    }
}
