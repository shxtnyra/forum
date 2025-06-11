package com.shxtnyra.forum.mapper;

import com.shxtnyra.forum.dto.report.*;
import com.shxtnyra.forum.entity.CommentEntity;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.ReportEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.enums.ReportReason;

import java.util.Map;

public class ReportMapper {
    public static ReportDetailsDTO toDetailsDTO(ReportEntity report) {
        return ReportDetailsDTO.builder()
                .id(report.getId())
                .reason(report.getReason())
                .post(report.getPost() != null ? PostMapper.toShortDTO(report.getPost()) : null)
                .comment(report.getComment() != null ? CommentMapper.toShortDTO(report.getComment()) : null)
                .author(UserMapper.toShortDTO(report.getAuthor()))
                .reportedAt(report.getReportedAt())
                .solved(report.isSolved())
                .build();
    }

    public static ReportShortDTO toShortDTO(ReportEntity report) {
        return ReportShortDTO.builder()
                .id(report.getId())
                .reason(report.getReason())
                .solved(report.isSolved())
                .postId(report.getPost() != null ? report.getPost().getId() : null)
                .commentId(report.getComment() != null ? report.getComment().getId() : null)
                .build();
    }

    public static ReportPostDTO toReportPostDTO(PostEntity post, Long count, Map<ReportReason, Long> reports) {
        return ReportPostDTO.builder()
                .post(PostMapper.toShortDTO(post))
                .reportCount(count)
                .reports(reports)
                .build();
    }

    public static ReportCommentDTO toReportCommentDTO(CommentEntity comment, Long count, Map<ReportReason, Long> reports) {
        return ReportCommentDTO.builder()
                .comment(CommentMapper.toShortDTO(comment))
                .reportCount(count)
                .reports(reports)
                .build();
    }

    public static ReportUserDTO toReportUserDTO(UserEntity user, Long count, Map<ReportReason, Long> reports) {
        return ReportUserDTO.builder()
                .user(UserMapper.toShortDTO(user))
                .reportCount(count)
                .reports(reports)
                .build();
    }
}
