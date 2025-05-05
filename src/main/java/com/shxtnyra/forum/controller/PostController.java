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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> createPost(@RequestBody @Valid PostCreateDTO dto,
                                                     @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.createPost(dto, user));
    }

    @GetMapping
    public ResponseEntity<Page<PostShortDTO>> getAllPosts(Pageable pageable,
                                                            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/topics")
    public ResponseEntity<Page<PostShortDTO>> getPostsByTopic(
            @RequestParam String topicSlug,
            Pageable pageable
    ) {
        return ResponseEntity.ok(postService.getPostsByTopic(topicSlug, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsDTO> getPostById(@PathVariable Long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/feed")
    public ResponseEntity<Slice<PostShortDTO>> getNewsFeed(
            @RequestParam(required = false) Long lastSeenId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getNewsFeed(lastSeenId, pageable));
    }
}
