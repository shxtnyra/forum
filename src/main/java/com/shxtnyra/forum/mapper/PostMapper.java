package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.post.*;
import com.shxtnyra.forum.entity.PostEntity;
import java.util.stream.Collectors;

/**
 * Маппер для преобразования сущностей постов (PostEntity) в DTO объекты.
 * Обеспечивает конвертацию между слоями приложения.
 */
public class PostMapper {
    /**
     * Преобразует сущность поста в детальное DTO представление.
     * Включает все поля поста, включая блоки контента и метаданные.
     *
     * @param post сущность поста для преобразования
     * @return PostDetailsDTO с полной информацией о посте
     */
    public static PostDetailsDTO toDetailsDTO(PostEntity post) {
        return PostDetailsDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .customUri(post.getCustomUri())
                .blocks(post.getContentBlocks().stream()
                        .map(block -> ContentBlockDTO.builder()
                                .type(block.getType())
                                .cover(block.isCover())
                                .hidden(block.isHidden())
                                .anchor(block.getAnchor())
                                .data(ContentBlockDataDTO.builder()
                                        .text((String) block.getData().get("text"))
                                        .mediaUrl((String) block.getData().get("mediaUrl"))
                                        .caption((String) block.getData().get("caption"))
                                        .altText((String) block.getData().get("altText"))
                                        .videoId((String) block.getData().get("videoId"))
                                        .videoProvider((String) block.getData().get("videoProvider"))
                                        .linkUrl((String) block.getData().get("linkUrl"))
                                        .linkTitle((String) block.getData().get("linkTitle"))
                                        .linkDescription((String) block.getData().get("linkDescription"))
                                        .code((String) block.getData().get("code"))
                                        .language((String) block.getData().get("language"))
                                        .quoteAuthor((String) block.getData().get("quoteAuthor"))
                                        .pollQuestion((String) block.getData().get("pollQuestion"))
                                        .pollOptions((List<String>) block.getData().get("pollOptions"))
                                        .metadata(block.getData())
                                        .build())
                                .build())
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .author(UserMapper.toShortDTO(post.getAuthor()))
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .dislikeCount(post.getDislikeCount())
                .draft(post.isDraft())
                .build();
    }

    /**
     * Преобразует сущность поста в краткое DTO представление.
     * Содержит только основную информацию о посте, без деталей контента.
     *
     * @param post сущность поста для преобразования
     * @return PostShortDTO с базовой информацией о посте
     */
    public static PostShortDTO toShortDTO(PostEntity post) {
        return PostShortDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .customUri(post.getCustomUri())
                .createdAt(post.getCreatedAt())
                .author(UserMapper.toShortDTO(post.getAuthor()))
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .build();
    }
}
