package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.post.PostDTO;
import com.shxtnyra.forum.dto.post.PostPreviewDTO;
import com.shxtnyra.forum.entity.PostEntity;

public class PostMapper {
    public static PostDTO toDTO(PostEntity post) {
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createAt(post.getCreatedAt())
                .updateAt(post.getUpdatedAt())
                .author(UserMapper.toPreviewDTO(post.getAuthor()))
                .build();
    }

    public static PostPreviewDTO toPreviewDTO(PostEntity post) {
        return PostPreviewDTO.builder()
                .title(post.getTitle())
                .createAt(post.getCreatedAt())
                .author(UserMapper.toPreviewDTO(post.getAuthor()))
                .build();
    }
}
