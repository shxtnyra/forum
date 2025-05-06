package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.comment.CommentCreateDTO;
import com.shxtnyra.forum.dto.comment.CommentDetailsDTO;
import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated")
    public ResponseEntity<CommentDetailsDTO> createComment(@RequestBody @Valid CommentCreateDTO dto,
                                                           @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(commentService.createComment(dto, user));
    }

    // Получить комментарий
    @GetMapping
    public ResponseEntity<CommentDetailsDTO> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    // Получить все комментарии под постом
    @GetMapping
    public ResponseEntity<Page<CommentShortDTO>> getAllComments(@PathVariable Long id,
                                                                Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllComments(id, pageable));
    }

    // Получить все ответы на комментарий
    @GetMapping
    public ResponseEntity<Page<CommentShortDTO>> getAllReplies(@PathVariable Long id,
                                                               Pageable pageable) {
        return ResponseEntity.ok(commentService.getAllReplies(id, pageable));
    }

    // Получить комментарии по подстроке
    @GetMapping
    public ResponseEntity<List<CommentShortDTO>> getCommentsByText(String text) {
        return ResponseEntity.ok(commentService.getCommentsByText(text));
    }

    // Увеличить счетчик лайков
    @GetMapping
    public ResponseEntity<Void> incrementLikes(@PathVariable Long id,
                                               @AuthenticationPrincipal UserEntity user) {
        commentService.incrementLikes(id);
        return ResponseEntity.noContent().build();
    }

    // Увеличить счетчик дизлайков
    @GetMapping
    public ResponseEntity<Void> incrementDislikes(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserEntity user) {
        commentService.incrementDislikes(id);
        return ResponseEntity.noContent().build();
    }

    // Удалить комментарий
    @DeleteMapping
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @AuthenticationPrincipal UserEntity user) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
