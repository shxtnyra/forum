package com.shxtnyra.forum.dto.report;

import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.dto.user.UserShortDTO;
import com.shxtnyra.forum.enums.ReportReason;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportDetailsDTO {
    private final Long id;
    private final LocalDateTime reportedAt;
    private final UserShortDTO reporter;
    private final CommentShortDTO comment;
    private final ReportReason reason;
    private final boolean isSolved;
}
