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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentShortDTO> createComment(@RequestBody @Valid CommentCreateDTO dto,
                                                         @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(commentService.createComment(dto, user));
    }

    // Получить все комментарии под постом
    @GetMapping("/post/{id}")
    public ResponseEntity<List<CommentShortDTO>> getAllCommentsByPost(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsByPost(id));
    }

    // Получить все ответы на комментарий
    @GetMapping("/parent/{id}")
    public ResponseEntity<List<CommentShortDTO>> getCommentsByParent(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentsByParent(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Page<CommentShortDTO>> getCommentsByAuthor(
            @PathVariable Long id,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(commentService.getCommentsByAuthor(id, pageable));
    }

    // TODO спорный эндпоинт
    @GetMapping("/{id}")
    public ResponseEntity<CommentDetailsDTO> getCommentById(@PathVariable Long id) {
        return ResponseEntity.ok(commentService.getCommentById(id));
    }

    // Удалить комментарий
    // TODO Сделать мягкое удаление, но это когда всеми удалениями займусь
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id,
                                              @AuthenticationPrincipal UserEntity user) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
