package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.CommentRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для работы с рейтингом комментариев.
 * Предоставляет API для оценки комментария (лайки/дизлайки).
 */
@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/v1/posts/{postId}/comments/{commentId}")
@RequiredArgsConstructor
public class CommentRatingController {
    private final CommentRatingService ratingService;

    /**
     * Поставить лайк комментарию.
     *
     * @param commentId идентификатор комментария
     * @param user текущий аутентифицированный пользователь
     * @implNote Один пользователь может оценить комментарий только один раз.
     *           Повторный вызов отменит предыдущую оценку.
     */
    @PostMapping("/like")
    public void likeComment(@PathVariable Long commentId,
                            @AuthenticationPrincipal UserEntity user) {
        ratingService.rateComment(commentId, user, true);
    }

    /**
     *
     * Поставить дизлайк комментарию.
     *
     * @param commentId идентификатор комментария
     * @param user текущий аутентифицированный пользователь
     * @implNote Один пользователь может оценить комментарий только один раз.
     *           Повторный вызов отменит предыдущую оценку.
     */
    @PostMapping("/dislike")
    public void dislikeComment(@PathVariable Long commentId,
                               @AuthenticationPrincipal UserEntity user) {
        ratingService.rateComment(commentId, user, false);
    }
}
