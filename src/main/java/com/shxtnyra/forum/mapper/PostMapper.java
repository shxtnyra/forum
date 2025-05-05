package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.PostEntity;

public class PostMapper {
    public static PostDetailsDTO toDetailsDTO(PostEntity post) {
        return PostDetailsDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createAt(post.getCreatedAt())
                .updateAt(post.getUpdatedAt())
                .author(UserMapper.toShortDTO(post.getAuthor()))
                .build();
    }

    public static PostShortDTO toShortDTO(PostEntity post) {
        return PostShortDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .createAt(post.getCreatedAt())
                .author(UserMapper.toShortDTO(post.getAuthor()))
                .build();
    }
}
