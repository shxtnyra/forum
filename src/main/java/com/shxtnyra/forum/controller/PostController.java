package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDTO;
import com.shxtnyra.forum.dto.post.PostPreviewDTO;
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

    @GetMapping
    public ResponseEntity<Page<PostPreviewDTO>> getAllPosts(Pageable pageable,
                                                            @AuthenticationPrincipal UserEntity user) {
        System.out.println(user);
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("{id}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/feed")
    public ResponseEntity<Slice<PostPreviewDTO>> getNewsFeed(
            @RequestParam(required = false) Long lastSeenId,
            Pageable pageable) {
        return ResponseEntity.ok(postService.getNewsFeed(lastSeenId, pageable));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDTO> createPost(@RequestBody @Valid PostCreateDTO dto,
                                              @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.createPost(dto, user));
    }
}
