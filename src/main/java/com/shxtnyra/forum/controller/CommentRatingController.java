package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.CommentRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts/{postId}/comments/{commentId}")
@RequiredArgsConstructor
public class CommentRatingController {
    private final CommentRatingService ratingService;

    @PostMapping("/like")
    public void likeComment(@PathVariable Long commentId,
                            @AuthenticationPrincipal UserEntity user) {
        ratingService.rateComment(commentId, user, true);
    }

    @PostMapping("/dislike")
    public void dislikeComment(@PathVariable Long commentId,
                               @AuthenticationPrincipal UserEntity user) {
        ratingService.rateComment(commentId, user, false);
    }
}
