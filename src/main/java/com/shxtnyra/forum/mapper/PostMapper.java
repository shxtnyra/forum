package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.PostEntity;

import java.util.stream.Collectors;

public class PostMapper {
    public static PostDetailsDTO toDetailsDTO(PostEntity post) {
        return PostDetailsDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .previewContent(post.getPreviewContent())
                .createAt(post.getCreatedAt())
                .updateAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .author(UserMapper.toShortDTO(post.getAuthor()))
                .status(post.getStatus())
                .coverImageUrl(post.getCoverImageUrl())
                .tags(post.getTags().stream()
                        .map(TagMapper::toDTO)
                        .collect(Collectors.toSet()))
                .isPinned(post.isPinned())
                .isPromoted(post.isPromoted())
                .isEditorChoice(post.isEditorChoice())
                .metaDescription(post.getMetaDescription())
                .metaKeywords(post.getMetaKeywords())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .dislikeCount(post.getDislikeCount())
                .build();
    }

    public static PostShortDTO toShortDTO(PostEntity post) {
        return PostShortDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .createAt(post.getCreatedAt())
                .author(UserMapper.toShortDTO(post.getAuthor()))
                .previewContent(post.getPreviewContent())
                .coverImageUrl(post.getCoverImageUrl())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .dislikeCount(post.getDislikeCount())
                .build();
    }
}
