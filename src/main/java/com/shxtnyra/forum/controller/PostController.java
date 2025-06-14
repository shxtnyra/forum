package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Контроллер для работы с постами.
 * Предоставляет API для управления черновиками, публикацией и получением постов.
 */
@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    /**
     * Получить черновики текущего пользователя.
     *
     * @param user текущий аутентифицированный пользователь
     * @param pageable параметры пагинации
     * @return Page<PostShortDTO> страница черновиков
     */
    @GetMapping("/drafts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PostShortDTO>> getDraftPosts(@AuthenticationPrincipal UserEntity user,
                                                            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDrafts(user, pageable));
    }

    /**
     * Создать черновик поста.
     *
     * @param dto данные для создания поста
     * @param user текущий аутентифицированный пользователь
     * @return PostDetailsDTO созданный черновик
     */
    @PostMapping("/drafts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> createDraft(
            @RequestBody @Valid PostCreateDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.createPost(dto, user));
    }

    /**
     * Редактировать черновик поста.
     *
     * @param id идентификатор поста
     * @param dto данные для редактирования поста
     * @param user текущий аутентифицированный пользователь
     * @return PostDetailsDTO обновленный черновик
     */
    @PatchMapping("/drafts/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> editDraft(@PathVariable Long id,
                                          @RequestBody @Valid PostCreateDTO dto,
                                          @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.editDraft(id, dto, user));
    }

    /**
     * Опубликовать черновик поста.
     *
     * @param id идентификатор поста
     * @param user текущий аутентифицированный пользователь
     * @return PostDetailsDTO опубликованный пост
     */
    @PostMapping("/drafts/{id}/release")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> releasePost(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserEntity user) {

        return ResponseEntity.ok(postService.releasePost(id, user));
    }

    /**
     * Получить пост по идентификатору.
     *
     * @param id идентификатор поста
     * @param user текущий аутентифицированный пользователь
     * @return PostDetailsDTO данные поста
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsDTO> getPostById(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.getPostById(id, user));
    }

    /**
     * Получить ленту постов.
     *
     * @param lastSeenId идентификатор последнего просмотренного поста
     * @param topic тема постов
     * @param period период времени для фильтрации постов
     * @param sort тип сортировки ("newest" или "top")
     * @param limit максимальное количество постов
     * @return Slice<PostShortDTO> лента постов
     */
    @GetMapping()
    public ResponseEntity<Slice<PostShortDTO>> getPostFeed(
            @RequestParam(required = false) Long lastSeenId,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false, defaultValue = "day") String period,
            @RequestParam(required = false, defaultValue = "newest") String sort,
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {

        // Валидация параметров
        if (limit > 20)
            limit = 20;

        if (period != null && !List.of("day", "week", "month", "year", "all").contains(period)) {
            throw new IllegalArgumentException("Invalid period value");
        }

        if (sort != null && !List.of("newest", "top").contains(sort)) {
            throw new IllegalArgumentException("Invalid sort value");
        }

        return ResponseEntity.ok(postService.getNewsFeed(lastSeenId, topic, period, sort, limit));
    }
}
