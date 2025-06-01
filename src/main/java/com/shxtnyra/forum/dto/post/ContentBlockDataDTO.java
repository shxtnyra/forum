package com.shxtnyra.forum.dto.post;

import lombok.Builder;
import java.util.List;
import java.util.Map;

@Builder
public record ContentBlockDataDTO(
    String text,
    String mediaUrl,
    String caption,
    String altText,
    String videoId,
    String videoProvider,
    String linkUrl,
    String linkTitle,
    String linkDescription,
    String code,
    String language,
    String quoteAuthor,
    String pollQuestion,
    List<String> pollOptions,
    Map<String, Object> metadata
) {}