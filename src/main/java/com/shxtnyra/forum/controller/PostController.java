package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.post.*;
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
 * Контроллер для управления постами.
 * Предоставляет REST API для создания, обновления, удаления и получения постов,
 * а также для работы с черновиками и поиска.
 */
@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    /**
     * Создает новый пост.
     * Требуется аутентификация пользователя.
     *
     * @param dto данные для создания поста
     * @param user текущий пользователь
     * @return созданный пост
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> createPost(
            @RequestBody @Valid PostCreateDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.createPost(dto, user));
    }

    /**
     * Создает новый черновик.
     * Требуется аутентификация пользователя.
     *
     * @param dto данные для создания черновика
     * @param user текущий пользователь
     * @return созданный черновик
     */
    @PostMapping("/drafts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> createDraft(
            @RequestBody @Valid PostCreateDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.createDraft(dto, user));
    }

    /**
     * Обновляет существующий черновик.
     * Требуется аутентификация пользователя.
     *
     * @param id ID черновика
     * @param dto новые данные
     * @param user текущий пользователь
     * @return обновленный черновик
     */
    @PatchMapping("/drafts/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> updateDraft(
            @PathVariable Long id,
            @RequestBody @Valid PostCreateDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.updateDraft(id, dto, user));
    }

    /**
     * Публикует черновик.
     * Требуется аутентификация пользователя.
     *
     * @param id ID черновика
     * @param user текущий пользователь
     * @return опубликованный пост
     */
    @PatchMapping("/drafts/{id}/publish")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> publishDraft(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.publishDraft(id, user));
    }

    /**
     * Получает черновики текущего пользователя.
     * Требуется аутентификация пользователя.
     *
     * @param user текущий пользователь
     * @param pageable параметры пагинации
     * @return страница черновиков
     */
    @GetMapping("/drafts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PostShortDTO>> getUserDrafts(
            @AuthenticationPrincipal UserEntity user,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getUserDrafts(user, pageable));
    }

    /**
     * Получает пост по ID.
     * Доступно всем пользователям.
     *
     * @param id ID поста
     * @param currentUserId ID текущего пользователя (может быть null)
     * @return найденный пост
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsDTO> getPost(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "id") Long currentUserId) {
        return ResponseEntity.ok(postService.getPost(id, currentUserId));
    }

    /**
     * Получает ленту постов.
     * Поддерживает фильтрацию по теме, периоду и сортировку.
     * Доступно всем пользователям.
     *
     * @param lastSeenId ID последнего просмотренного поста
     * @param topicId ID темы для фильтрации
     * @param period период времени (hour, day, week, month, year, all)
     * @param sort тип сортировки (new, top, hot, best)
     * @param limit максимальное количество постов (макс. 50)
     * @param includeDrafts включать ли черновики
     * @return срез постов
     */
    @GetMapping("/feed")
    public ResponseEntity<Slice<PostShortDTO>> getFeed(
            @RequestParam(required = false) Long lastSeenId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "false") boolean includeDrafts) {
        
        validateFeedParams(period, sort, limit);
        
        return ResponseEntity.ok(postService.getFeed(
            lastSeenId,
            topicId,
            period,
            sort,
            limit,
            includeDrafts
        ));
    }

    /**
     * Проверяет корректность параметров для получения ленты.
     *
     * @param period период времени
     * @param sort тип сортировки
     * @param limit максимальное количество постов
     * @throws IllegalArgumentException если параметры некорректны
     */
    private void validateFeedParams(String period, String sort, int limit) {
        if (limit > 50) limit = 50;
        
        if (period != null && !List.of("hour", "day", "week", "month", "year", "all").contains(period)) {
            throw new IllegalArgumentException("Invalid period value");
        }
        
        if (sort != null && !List.of("new", "top", "hot", "best").contains(sort)) {
            throw new IllegalArgumentException("Invalid sort value");
        }
    }

    /**
     * Ищет посты по заголовку.
     * Поддерживает фильтрацию по теме.
     * Доступно всем пользователям.
     *
     * @param query поисковый запрос
     * @param topicId ID темы для фильтрации
     * @param pageable параметры пагинации
     * @return страница найденных постов
     */
    @GetMapping("/search")
    public ResponseEntity<Page<PostShortDTO>> searchPosts(
            @RequestParam String query,
            @RequestParam(required = false) Long topicId,
            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.searchPosts(query, topicId, pageable));
    }
}