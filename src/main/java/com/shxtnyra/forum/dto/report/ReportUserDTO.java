package com.shxtnyra.forum.dto.report;

import com.shxtnyra.forum.dto.user.UserShortDTO;
import com.shxtnyra.forum.enums.ReportReason;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ReportUserDTO {
    private final UserShortDTO user;
    private final Long reportCount;
    private final Map<ReportReason, Long> reports;
}
