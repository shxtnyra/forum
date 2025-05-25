package com.shxtnyra.forum.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PostCreateDTO {
    @NotBlank
    @Size(max = 200)
    private final String title;

    @NotBlank
    private final String content;

    @Size(max = 500)
    private final String previewContent;

    @NotNull
    private final Long topicId;

    private final String coverImageUrl;

    private final Set<String> tags;

    @Size(max = 200)
    private final String metaDescription;

    @Size(max = 200)
    private final String metaKeywords;
}
