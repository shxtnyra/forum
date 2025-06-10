package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.dto.report.ReportCreateDTO;
import com.shxtnyra.forum.dto.report.ReportDetailsDTO;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.service.PostService;
import com.shxtnyra.forum.service.ReportService;
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

@RestController
@RequestMapping("/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final ReportService reportService;

    @PostMapping("/drafts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<PostDetailsDTO> createDraft(
            @RequestBody @Valid PostCreateDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.createPost(dto, user));
    }

    @PatchMapping("/drafts/{id}")
    public ResponseEntity<PostDetailsDTO> editDraft(@PathVariable Long id,
                                          @RequestBody @Valid PostCreateDTO dto,
                                          @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.editDraft(id, dto, user));
    }

    @PatchMapping("/drafts/{id}/release")
    public ResponseEntity<PostDetailsDTO> releasePost(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserEntity user) {
        ;
        return ResponseEntity.ok(postService.releasePost(id, user));
    }

    @GetMapping("/drafts")
    public ResponseEntity<Page<PostShortDTO>> getDraftPosts(@AuthenticationPrincipal UserEntity user,
                                                            @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(postService.getDrafts(user, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDetailsDTO> getPostById(@PathVariable Long id,
                                                      @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(postService.getPostById(id, user));
    }

    @GetMapping()
    public ResponseEntity<Slice<PostShortDTO>> getPostFeed(
            @RequestParam(required = false) Long lastSeenId,
            @RequestParam(required = false) String topic,
            @RequestParam(required = false, defaultValue = "day") String period,
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

    @PostMapping("/{postId}/reports")
    public ResponseEntity<ReportDetailsDTO> createReport(@RequestBody @Valid ReportCreateDTO createDTO,
                                                         @AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(reportService.createReport(createDTO, user));
    }
}
