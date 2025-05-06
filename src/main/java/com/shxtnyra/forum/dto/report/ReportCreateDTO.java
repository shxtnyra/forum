package com.shxtnyra.forum.dto.report;

import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.enums.ReportReason;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportCreateDTO {
    private final CommentShortDTO comment;
    @NotBlank
    private final ReportReason reason;
}
