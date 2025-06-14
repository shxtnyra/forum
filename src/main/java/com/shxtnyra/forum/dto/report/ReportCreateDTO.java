package com.shxtnyra.forum.dto.report;

import com.shxtnyra.forum.enums.ReportReason;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportCreateDTO {
    @NotNull
    private final ReportReason reason;
}
