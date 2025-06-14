package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.report.*;
import com.shxtnyra.forum.entity.CommentEntity;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.ReportEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.enums.ReportReason;
import com.shxtnyra.forum.exception.exceptions.AccessDeniedException;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.ReportMapper;
import com.shxtnyra.forum.repository.CommentRepository;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.ReportRepository;
import com.shxtnyra.forum.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportDetailsDTO createReportOnPost(Long postId, ReportCreateDTO createDTO, UserEntity user) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        if (post.isDraft() || post.isDeleted() || post.isInvisible()) {
            throw new AccessDeniedException("Нельзя пожаловаться на этот пост");
        }

        if (post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Нельзя пожаловаться на свой пост");
        }

        if (reportRepository.getExistingPostReportByUserAndReason(user.getId(),
                createDTO.getReason(),
                postId).isPresent()) {
            throw new IllegalStateException("Нельзя отправить более одной одинаковой жалобы на пост");
        }
        ReportEntity report = ReportEntity.builder()
                .post(post)
                .reason(createDTO.getReason())
                .author(user)
                .build();
        report = reportRepository.save(report);
        return ReportMapper.toDetailsDTO(report);
    }

    @Transactional
    public ReportDetailsDTO createReportOnComment(Long commentId, ReportCreateDTO createDTO, UserEntity user) {
        CommentEntity comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));

        if (comment.isDeleted()) {
            throw new AccessDeniedException("Нельзя пожаловаться на этот комментарий");
        }

        if (comment.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Нельзя пожаловаться на свой комментарий");
        }

        if (reportRepository.getExistingCommentReportByUserAndReason(user.getId(),
                createDTO.getReason(),
                commentId).isPresent()) {
            throw new IllegalStateException("Нельзя отправить более одной одинаковой жалобы на комментарий");
        }

        ReportEntity report = ReportEntity.builder()
                .comment(comment)
                .reason(createDTO.getReason())
                .author(user)
                .build();
                
        report = reportRepository.save(report);
        return ReportMapper.toDetailsDTO(report);
    }

    // Все жалобы пользователя
    public ReportUserDTO getAllReportsByUserId(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        List<ReportEntity> reports = reportRepository.getAllReportsByUserId(userId);

        // Группируем жалобы по причинам
        Map<ReportReason, Long> reasonsCount = reports.stream()
                .collect(Collectors.groupingBy(
                        ReportEntity::getReason,
                        Collectors.counting()
                ));

        return ReportMapper.toReportUserDTO(user, (long) reports.size(), reasonsCount);
    }

    // Все жалобы на пользователя
    public ReportUserDTO getAllReportsOnUserById(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        List<ReportEntity> postReports = reportRepository.getPostReportsOnUserById(userId);
        List<ReportEntity> commentReports = reportRepository.getCommentReportsOnUserById(userId);

        List<ReportEntity> reports = new ArrayList<>();
        reports.addAll(postReports);
        reports.addAll(commentReports);

        Map<ReportReason, Long> reasonsCount = reports.stream()
                .collect(Collectors.groupingBy(
                        ReportEntity::getReason,
                        Collectors.counting()
                ));

        return ReportMapper.toReportUserDTO(user, (long) reports.size(), reasonsCount);
    }

    public ReportPostDTO getAllPostReportsByPost(Long postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        List<ReportEntity> reports = reportRepository.getAllPostReportsByPost(postId);

        if (reports.isEmpty()) {
            return ReportMapper.toReportPostDTO(post, 0L, Collections.emptyMap());
        }

        Map<ReportReason, Long> reasonsCount = reports.stream()
                .collect(Collectors.groupingBy(
                        ReportEntity::getReason,
                        Collectors.counting()
                ));

        return ReportMapper.toReportPostDTO(post, (long) reports.size(), reasonsCount);
    }

    public Page<ReportPostDTO> getAllPostReports(Pageable pageable) {
        List<ReportEntity> reports = reportRepository.getAllPostReports();

        if (reports.isEmpty()) {
            return new PageImpl<>(
                    Collections.emptyList(),
                    pageable,
                    0L
            );
        }

        // Группировка репортов в формате {Post1: [report1, report2, ...]}
        Map<PostEntity, List<ReportEntity>> reportsByPost = reports
                .stream()
                .collect(Collectors.groupingBy(ReportEntity::getPost));

        List<ReportPostDTO> dto = reportsByPost.entrySet().stream()
                .map(entry -> {
                    PostEntity post = entry.getKey();
                    List<ReportEntity> postReports = entry.getValue();

                    // Считаем количество жалоб по каждой причине
                    Map<ReportReason, Long> reasonsCount = postReports.stream()
                            .collect(Collectors.groupingBy(
                                    ReportEntity::getReason,
                                    Collectors.counting()
                            ));

                    return ReportMapper.toReportPostDTO(post,
                            (long) postReports.size(),
                            reasonsCount);
                })
                .toList();

        // Сортировка по общему числу жалоб (по убыванию)
        dto = dto.stream()
                .sorted(Comparator.comparingLong(ReportPostDTO::getReportCount).reversed())
                .toList();

        return new PageImpl<>(
                dto,
                pageable,
                dto.size()
        );
    }

    public Page<ReportCommentDTO> getAllCommentReports(Long postId, Pageable pageable) {
        List<ReportEntity> reports;

        if (postId != null) {
            if (!postRepository.existsById(postId)) {
                throw new EntityNotFoundException("Пост не найден");
            }
            reports = reportRepository.getAllCommentReportsByPost(postId);
        } else {
            reports = reportRepository.getAllCommentReports();
        }

        if (reports.isEmpty()) {
            return new PageImpl<>(
                    Collections.emptyList(),
                    pageable,
                    0L
            );
        }

        // Группировка репортов в формате {Comment1: [report1, report2, ...]}
        Map<CommentEntity, List<ReportEntity>> reportsByComment = reports
                .stream()
                .collect(Collectors.groupingBy(ReportEntity::getComment));

        List<ReportCommentDTO> dto = reportsByComment.entrySet().stream()
                .map(entry -> {
                    CommentEntity comment = entry.getKey();
                    List<ReportEntity> commentReports = entry.getValue();

                    // Считаем количество жалоб по каждой причине
                    Map<ReportReason, Long> reasonsCount = commentReports.stream()
                            .collect(Collectors.groupingBy(
                                    ReportEntity::getReason,
                                    Collectors.counting()
                            ));

                    return ReportMapper.toReportCommentDTO(comment,
                            (long) commentReports.size(),
                            reasonsCount);
                })
                .toList();

        // Сортировка по общему числу жалоб (по убыванию)
        dto = dto.stream()
                .sorted(Comparator.comparingLong(ReportCommentDTO::getReportCount).reversed())
                .toList();

        return new PageImpl<>(
                dto,
                pageable,
                dto.size()
        );
    }

    @Transactional
    public void changePostReportStatus(Long postId, ReportReason reportReason, boolean newStatus) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Пост не найден");
        }

        reportRepository.setSolvedByPostByReason(postId, reportReason, newStatus);
    }

    @Transactional
    public void changeCommentReportStatus(Long commentId, ReportReason reportReason, boolean newStatus) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Комментарий не найден");
        }

        reportRepository.setSolvedByCommentByReason(commentId, reportReason, newStatus);
    }
}
