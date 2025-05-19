package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.PostRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/posts/{postId}")
@RequiredArgsConstructor
public class PostRatingController {
    private final PostRatingService postRatingService;

    @PostMapping("/like")
    public void likePost(@PathVariable Long postId,
                         @AuthenticationPrincipal UserEntity user) {
        postRatingService.ratePost(postId, user, true);
    }

    @PostMapping("/dislike")
    public void dislikePost(@PathVariable Long postId,
                            @AuthenticationPrincipal UserEntity user) {
        postRatingService.ratePost(postId, user, false);
    }
}
