package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.comment.CommentDetailsDTO;
import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.entity.CommentEntity;

public class CommentMapper {
    public static CommentDetailsDTO toDetailsDTO(CommentEntity comment) {
        return CommentDetailsDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .author(UserMapper.toShortDTO(comment.getAuthor()))
                .post(PostMapper.toShortDTO(comment.getPost()))
                .parent(CommentMapper.toShortDTO(comment.getParent()))
                .build();
    }

    public static CommentShortDTO toShortDTO(CommentEntity comment) {
        return CommentShortDTO.builder()
                .id(comment.getId())
                .createdAt(comment.getCreatedAt())
                .author(UserMapper.toShortDTO(comment.getAuthor()))
                .build();
    }
}
