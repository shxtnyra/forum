package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.comment.CommentCreateDTO;
import com.shxtnyra.forum.dto.comment.CommentDetailsDTO;
import com.shxtnyra.forum.dto.comment.CommentShortDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommentShortDTO> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentCreateDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(commentService.createComment(dto, user));
    }

    @GetMapping
    public ResponseEntity<List<CommentShortDTO>> getCommentsByPost(@PathVariable Long postId,
                                                                   @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(commentService.getCommentsByPost(postId, user));
    }

    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentShortDTO>> getCommentReplies(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(commentService.getCommentsByParent(commentId, user));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDetailsDTO> getCommentById(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(commentService.getCommentById(commentId, user));
    }
}
