package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.PostRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с рейтингом постов.
 * Предоставляет API для оценки постов (лайки/дизлайки).
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/v1/posts/{postId}")
@RequiredArgsConstructor
public class PostRatingController {
    private final PostRatingService postRatingService;

    /**
     * Поставить лайк посту.
     *
     * @param postId идентификатор поста
     * @param user текущий аутентифицированный пользователь
     * @implNote Один пользователь может оценить пост только один раз.
     *           Повторный вызов отменит предыдущую оценку.
     */
    @PostMapping("/like")
    public void likePost(@PathVariable Long postId,
                         @AuthenticationPrincipal UserEntity user) {
        postRatingService.ratePost(postId, user, true);
    }

    /**
     * Поставить дизлайк посту.
     *
     * @param postId идентификатор поста
     * @param user текущий аутентифицированный пользователь
     * @implNote Один пользователь может оценить пост только один раз.
     *           Повторный вызов отменит предыдущую оценку.
     */
    @PostMapping("/dislike")
    public void dislikePost(@PathVariable Long postId,
                            @AuthenticationPrincipal UserEntity user) {
        postRatingService.ratePost(postId, user, false);
    }
}