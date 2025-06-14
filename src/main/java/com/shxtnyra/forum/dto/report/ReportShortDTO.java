package com.shxtnyra.forum.dto.report;

import com.shxtnyra.forum.enums.ReportReason;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportShortDTO {
    private final Long id;
    private final ReportReason reason;
    private final Long postId;
    private final Long commentId;
    private final boolean solved;
}
