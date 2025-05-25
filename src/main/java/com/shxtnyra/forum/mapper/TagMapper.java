package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.tag.TagDTO;
import com.shxtnyra.forum.entity.TagEntity;

public class TagMapper {
    public static TagDTO toDTO(TagEntity tag) {
        return TagDTO.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .build();
    }
} 