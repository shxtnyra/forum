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

/**
 * Контроллер для административных действий.
 * Предоставляет API для управления контентом, модерации и работы с жалобами.
 * Требует соответствующих прав доступа (ADMIN или MODERATOR).
 */
@RestController
@RequestMapping("/v1/administration")
@RequiredArgsConstructor
public class AdministrationController {
    private final TopicService topicService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReportService reportService;

    // === Topic Management ===

    /**
     * Создать новую тему.
     *
     * @param request DTO с данными для создания темы
     * @return TopicDetailsDTO созданная тема с полными данными
     * @apiNote Требуется роль ADMIN
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/topics")
    public ResponseEntity<TopicDetailsDTO> createTopic(@RequestBody TopicCreateDTO request) {
        return ResponseEntity.ok(topicService.createTopic(request));
    }

    /**
     * Удалить тему по идентификатору.
     *
     * @param id идентификатор темы
     * @return HTTP 204 No Content при успешном удалении
     * @apiNote Требуется роль ADMIN. Удаление физическое (без возможности восстановления)
     */
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopicById(id);
        return ResponseEntity.noContent().build();
    }

    // === Post Management ===

    /**
     * Получить посты пользователя с возможностью включения скрытых.
     *
     * @param userId идентификатор пользователя
     * @param includeInvisible включать ли скрытые посты
     * @param pageable параметры пагинации
     * @return Page<PostShortDTO> страница постов
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<Page<PostShortDTO>> getPostsByUser(@PathVariable Long userId,
                                                             @RequestParam boolean includeInvisible,
                                                             @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUserByVisibility(userId, includeInvisible, pageable));
    }

    /**
     * Получить черновики пользователя.
     *
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации
     * @return Page<PostShortDTO> страница черновиков
     * @apiNote Требуется роль ADMIN
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/drafts")
    public ResponseEntity<Page<PostShortDTO>> getDraftsByUserId(@PathVariable Long userId,
                                                                @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDraftsByUserId(userId, pageable));
    }

    /**
     * Получить удаленные посты пользователя.
     *
     * @param userId идентификатор пользователя
     * @param pageable параметры пагинации
     * @return Page<PostShortDTO> страница удаленных постов
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/posts/deleted")
    public ResponseEntity<Page<PostShortDTO>> getDeletedPostsByUser(@PathVariable Long userId,
                                                                    @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDeletedPostsByUser(userId, pageable));
    }

    /**
     * Получить все скрытые посты.
     *
     * @param pageable параметры пагинации
     * @return Page<PostShortDTO> страница скрытых постов
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/invisible")
    public ResponseEntity<Page<PostShortDTO>> getInvisiblePosts(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getInvisiblePosts(pageable));
    }

    /**
     * Изменить видимость поста.
     *
     * @param postId идентификатор поста
     * @param isInvisible сделать пост скрытым (true) или видимым (false)
     * @return HTTP 204 No Content при успешном изменении
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/visibility")
    public ResponseEntity<Void> changePostVisibility(@PathVariable Long postId,
                                                     @RequestParam boolean isInvisible) {
        postService.changeVisibility(postId, isInvisible);
        return ResponseEntity.noContent().build();
    }

    /**
     * Мягкое удаление поста.
     *
     * @param postId идентификатор поста
     * @return HTTP 204 No Content при успешном удалении
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePostSoft(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстановить мягко удаленный пост.
     *
     * @param postId идентификатор поста
     * @return HTTP 204 No Content при успешном восстановлении
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/recover")
    public ResponseEntity<Void> recoverPost(@PathVariable Long postId) {
        postService.recoverPost(postId);
        return ResponseEntity.noContent().build();
    }

    // === Comment Management ===

    /**
     * Получить комментарии пользователя с возможностью включения скрытых.
     *
     * @param userId идентификатор пользователя
     * @param includeInvisible включать ли скрытые комментарии
     * @param pageable параметры пагинации
     * @return Page<CommentShortDTO> страница комментариев
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/comments")
    public ResponseEntity<Page<CommentShortDTO>> getCommentsByAuthor(@PathVariable Long userId,
                                                                     @RequestParam boolean includeInvisible,
                                                                     @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(commentService.getCommentsByAuthorByVisibility(userId, includeInvisible, pageable));
    }

    /**
     * Мягкое удаление комментария.
     *
     * @param commentId идентификатор комментария
     * @return HTTP 204 No Content при успешном удалении
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteCommentSoft(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Восстановить мягко удаленный комментарий.
     *
     * @param commentId идентификатор комментария
     * @return HTTP 204 No Content при успешном восстановлении
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/comments/{commentId}/recover")
    public ResponseEntity<Void> recoverComment(@PathVariable Long commentId) {
        commentService.recoverComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // === Report Management ===

    /**
     * Получить все жалобы, отправленные пользователем.
     *
     * @param userId идентификатор пользователя
     * @return ReportUserDTO список жалоб пользователя
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/reports")
    public ResponseEntity<ReportUserDTO> getAllReportsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getAllReportsByUserId(userId));
    }

    /**
     * Получить все жалобы на пользователя.
     *
     * @param userId идентификатор пользователя
     * @return ReportUserDTO список жалоб на пользователя
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/reports/{userId}")
    public ResponseEntity<ReportUserDTO> getAllReportsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getAllReportsOnUserById(userId));
    }

    /**
     * Получить все жалобы на посты.
     *
     * @param pageable параметры пагинации
     * @return Page<ReportPostDTO> страница жалоб на посты
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/reports")
    public ResponseEntity<Page<ReportPostDTO>> getAllPostReports(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllPostReports(pageable));
    }

    /**
     * Получить все жалобы на конкретный пост.
     *
     * @param postId идентификатор поста
     * @return ReportPostDTO информация о жалобах на пост
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/{postId}/reports")
    public ResponseEntity<ReportPostDTO> getAllPostReportsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(reportService.getAllPostReportsByPost(postId));
    }

    /**
     * Изменить статус жалобы на пост.
     *
     * @param postId идентификатор поста
     * @param reportReason причина жалобы
     * @param newStatus новый статус жалобы
     * @return HTTP 204 No Content при успешном изменении
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/reports")
    public ResponseEntity<Void> changePostReportStatus(@PathVariable Long postId,
                                                       @RequestParam ReportReason reportReason,
                                                       @RequestParam boolean newStatus) {
        reportService.changePostReportStatus(postId, reportReason, newStatus);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получить все жалобы на комментарии.
     *
     * @param pageable параметры пагинации
     * @return Page<ReportCommentDTO> страница жалоб на комментарии
     * @apiNote Требуется роль ADMIN или MODERATOR. Если postId не указан, возвращаются все жалобы
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/comments/reports")
    public ResponseEntity<Page<ReportCommentDTO>> getAllCommentReports(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllCommentReports(null, pageable));
    }

    /**
     * Получить жалобы на комментарии конкретного поста.
     *
     * @param postId идентификатор поста
     * @param pageable параметры пагинации
     * @return Page<ReportCommentDTO> страница жалоб на комментарии
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/{postId}/comments/reports")
    public ResponseEntity<Page<ReportCommentDTO>> getAllCommentReportsByPost(@PathVariable Long postId,
                                                                             @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllCommentReports(postId, pageable));
    }

    /**
     * Изменить статус жалобы на комментарий.
     *
     * @param commentId идентификатор комментария
     * @param reportReason причина жалобы
     * @param newStatus новый статус жалобы
     * @return HTTP 204 No Content при успешном изменении
     * @apiNote Требуется роль ADMIN или MODERATOR
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/comments/{commentId}/reports")
    public ResponseEntity<Void> changeCommentReportStatus(@PathVariable Long commentId,
                                                          @RequestParam ReportReason reportReason,
                                                          @RequestParam boolean newStatus) {
        reportService.changeCommentReportStatus(commentId, reportReason, newStatus);
        return ResponseEntity.noContent().build();
    }
}