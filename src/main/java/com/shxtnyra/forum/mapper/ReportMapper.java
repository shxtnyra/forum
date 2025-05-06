package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.report.ReportDetailsDTO;
import com.shxtnyra.forum.dto.report.ReportShortDTO;
import com.shxtnyra.forum.entity.ReportEntity;

public class ReportMapper {
    public static ReportDetailsDTO toDetailsDTO(ReportEntity report) {
        return ReportDetailsDTO.builder()
                .id(report.getId())
                .reportedAt(report.getReportedAt())
                .reporter(UserMapper.toShortDTO(report.getReporter()))
                .comment(CommentMapper.toShortDTO(report.getComment()))
                .reason(report.getReason())
                .isSolved(report.isSolved())
                .build();
    }

    public static ReportShortDTO toShortDTO(ReportEntity report) {
        return ReportShortDTO.builder()
                .id(report.getId())
                .reportedAt(report.getReportedAt())
                .reporter(UserMapper.toShortDTO(report.getReporter()))
                .reason(report.getReason())
                .isSolved(report.isSolved())
                .build();
    }
}
