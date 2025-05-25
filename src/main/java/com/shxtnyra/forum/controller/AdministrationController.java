package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.dto.topic.TopicCreateDTO;
import com.shxtnyra.forum.dto.topic.TopicDetailsDTO;
import com.shxtnyra.forum.service.PostService;
import com.shxtnyra.forum.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/administration")
@RequiredArgsConstructor
public class AdministrationController {
    private final TopicService topicService;
    private final PostService postService;

    // === Topic Management ===
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/topics")
    public ResponseEntity<TopicDetailsDTO> createTopic(@RequestBody TopicCreateDTO request) {
        return ResponseEntity.ok(topicService.createTopic(request));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/topics/{id}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long id) {
        topicService.deleteTopicById(id);
        return ResponseEntity.noContent().build();
    }

    // Получить посты одного пользователя (простые посты либо вместе со скрытыми)
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<Page<PostShortDTO>> getPostsByUser(@PathVariable Long userId,
                                                             @RequestParam boolean includeInvisible,
                                                             @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getPostsByUserByVisibility(userId, includeInvisible, pageable));
    }

    // Получить черновики одного пользователя
    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/users/{userId}/drafts")
    public ResponseEntity<Page<PostShortDTO>> getDraftsByUserId(@PathVariable Long userId,
                                                                @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDraftsByUserId(userId, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/users/{userId}/posts/deleted")
    public ResponseEntity<Slice<PostShortDTO>> getDeletedPostsByUser(@PathVariable Long userId,
                                                                     @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDeletedPostsByUser(userId, pageable));
    }

    // Получить все скрытые посты
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/posts/invisible")
    public ResponseEntity<Page<PostShortDTO>> getInvisiblePosts(@PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getInvisiblePosts(pageable));
    }

    // Смена видимости поста
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/visibility")
    public ResponseEntity<Void> changePostVisibility(@PathVariable Long postId,
                                                     @RequestParam boolean isInvisible) {
        postService.changeVisibility(postId, isInvisible);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePostSoft(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PatchMapping("/posts/{postId}/recover")
    public ResponseEntity<Void> recoverPost(@PathVariable Long postId) {
        postService.recoverPost(postId);
        return ResponseEntity.noContent().build();
    }
}
