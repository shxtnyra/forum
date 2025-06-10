package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.dto.report.ReportCommentDTO;
import com.shxtnyra.forum.dto.report.ReportPostDTO;
import com.shxtnyra.forum.dto.report.ReportUserDTO;
import com.shxtnyra.forum.dto.topic.TopicCreateDTO;
import com.shxtnyra.forum.dto.topic.TopicDetailsDTO;
import com.shxtnyra.forum.enums.ReportReason;
import com.shxtnyra.forum.service.CommentService;
import com.shxtnyra.forum.service.PostService;
import com.shxtnyra.forum.service.ReportService;
import com.shxtnyra.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/administration")
@RequiredArgsConstructor
public class AdministrationController {
    private final TopicService topicService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReportService reportService;

    // === Topic Management ===
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/topics")
    public ResponseEntity<TopicDetailsDTO> createTopic(@RequestBody TopicCreateDTO request) {
        return ResponseEntity.ok(topicService.createTopic(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopicById(id);
        return ResponseEntity.noContent().build();
    }

    // Получить посты одного пользователя (простые посты либо вместе со скрытыми)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<Page<PostShortDTO>> getPostsByUser(@PathVariable Long userId,
                                                             @RequestParam boolean includeInvisible,
                                                             @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUserByVisibility(userId, includeInvisible, pageable));
    }

    // Получить черновики одного пользователя
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/users/{userId}/drafts")
    public ResponseEntity<Page<PostShortDTO>> getDraftsByUserId(@PathVariable Long userId,
                                                                @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDraftsByUserId(userId, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/posts/deleted")
    public ResponseEntity<Page<PostShortDTO>> getDeletedPostsByUser(@PathVariable Long userId,
                                                                    @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDeletedPostsByUser(userId, pageable));
    }

    /// Получить все жалобы от одного пользователя
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/reports")
    public ResponseEntity<ReportUserDTO> getAllReportsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getAllReportsByUserId(userId));
    }

    /// Получить все жалобы на одного пользователя
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/reports/{userId}")
    public ResponseEntity<ReportUserDTO> getAllReportsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getAllReportsOnUserById(userId));
    }

    // Получить все скрытые посты
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/invisible")
    public ResponseEntity<Page<PostShortDTO>> getInvisiblePosts(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getInvisiblePosts(pageable));
    }

    // Смена видимости поста
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/visibility")
    public ResponseEntity<Void> changePostVisibility(@PathVariable Long postId,
                                                     @RequestParam boolean isInvisible) {
        postService.changeVisibility(postId, isInvisible);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePostSoft(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/recover")
    public ResponseEntity<Void> recoverPost(@PathVariable Long postId) {
        postService.recoverPost(postId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/reports")
    public ResponseEntity<Page<ReportPostDTO>> getAllPostReports(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllPostReports(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/{postId}/reports")
    public ResponseEntity<ReportPostDTO> getAllPostReportsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(reportService.getAllPostReportsByPost(postId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/reports")
    public ResponseEntity<Void> changePostReportStatus(@PathVariable Long postId,
                                                       @RequestParam ReportReason reportReason,
                                                       @RequestParam boolean newStatus) {
        reportService.changePostReportStatus(postId, reportReason, newStatus);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/comments/reports")
    public ResponseEntity<Page<ReportCommentDTO>> getAllCommentReports(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllCommentReports(null, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/{postId}/comments/reports")
    public ResponseEntity<Page<ReportCommentDTO>> getAllCommentReportsByPost(@PathVariable Long postId,
                                                                             @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllCommentReports(postId, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/comments/{commentId}/reports")
    public ResponseEntity<Void> changeCommentReportStatus(@PathVariable Long commentId,
                                                          @RequestParam ReportReason reportReason,
                                                          @RequestParam boolean newStatus) {
        reportService.changeCommentReportStatus(commentId, reportReason, newStatus);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentSoft(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/comments/{commentId}/recover")
    public ResponseEntity<Void> recoverComment(@PathVariable Long commentId) {
        commentService.recoverComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // Получить комментарии одного пользователя (под обычными постами либо вместе со скрытыми/удаленными)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<Page<CommentShortDTO>> getCommentsByAuthor(@PathVariable Long userId,
                                                                     @RequestParam boolean includeInvisible,
                                                                     @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByAuthorByVisibility(userId, includeInvisible, pageable));
    }
}
