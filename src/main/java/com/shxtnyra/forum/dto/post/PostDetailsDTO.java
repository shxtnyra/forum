package com.shxtnyra.forum.dto.post;

import com.shxtnyra.forum.dto.tag.TagDTO;
import com.shxtnyra.forum.dto.user.UserShortDTO;
import com.shxtnyra.forum.enums.PostStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class PostDetailsDTO {
    private final Long id;
    private final String title;
    private final String content;
    private final String previewContent;
    private final LocalDateTime createAt;
    private final LocalDateTime updateAt;
    private final LocalDateTime publishedAt;
    private final UserShortDTO author;
    private final PostStatus status;
    private final String coverImageUrl;
    private final Set<TagDTO> tags;
    private final boolean isPinned;
    private final boolean isPromoted;
    private final boolean isEditorChoice;
    private final String metaDescription;
    private final String metaKeywords;
    private final int viewCount;
    private final int likeCount;
    private final int dislikeCount;
}
