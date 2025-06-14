package com.shxtnyra.forum.dto.report;

import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.enums.ReportReason;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ReportPostDTO {
    private final PostShortDTO post;
    private final Long reportCount;
    private final Map<ReportReason, Long> reports;
}
