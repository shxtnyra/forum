package com.shxtnyra.forum.dto.report;

import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.enums.ReportReason;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ReportCommentDTO {
    private final CommentShortDTO comment;
    private final Long reportCount;
    private final Map<ReportReason, Long> reports;
}
