package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.dto.tag.TagDTO;
import com.shxtnyra.forum.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @GetMapping("/search")
    public ResponseEntity<List<TagDTO>> searchTags(@RequestParam String query) {
        return ResponseEntity.ok(tagService.searchTags(query));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<TagDTO>> getTagsByPost(@PathVariable Long postId) {
        return ResponseEntity.ok(tagService.getTagsByPost(postId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PostMapping
    public ResponseEntity<TagDTO> createTag(
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(tagService.createTag(name, description));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PutMapping("/{id}")
    public ResponseEntity<TagDTO> updateTag(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam(required = false) String description) {
        return ResponseEntity.ok(tagService.updateTag(id, name, description));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @PutMapping("/post/{postId}")
    public ResponseEntity<Void> updatePostTags(
            @PathVariable Long postId,
            @RequestBody Set<String> tagNames) {
        tagService.updatePostTags(postId, tagNames);
        return ResponseEntity.noContent().build();
    }
} 