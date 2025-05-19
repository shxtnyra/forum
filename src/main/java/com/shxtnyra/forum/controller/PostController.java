package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // TODO это рудимент, будет отдельный эндпонит editor
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> createPost(
            @RequestBody @Valid PostCreateDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.createPost(dto, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsDTO> getPostById(@PathVariable Long id){
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping()
    public ResponseEntity<Slice<PostShortDTO>> getPostFeed(
            @RequestParam(required = false) Long lastSeenId,
            @RequestParam (required = false) String topic,
            @RequestParam (required = false, defaultValue = "day") String period,
            @RequestParam(required = false, defaultValue = "top") String sort,
            @RequestParam(required = false, defaultValue = "10") int limit
    ) {

        // Валидация параметров
        if (limit > 20)
            limit = 20;

        if (period != null && !List.of("day", "week", "month").contains(period)) {
            throw new IllegalArgumentException("Invalid period value");
        }

        if (sort != null && !List.of("newest", "top").contains(sort)) {
            throw new IllegalArgumentException("Invalid sort value");
        }

        return ResponseEntity.ok(postService.getNewsFeed(lastSeenId, topic, period, sort, limit));
    }
}
