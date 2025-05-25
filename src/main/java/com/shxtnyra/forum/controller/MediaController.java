package com.shxtnyra.forum.controller;

import com.shxtnyra.forum.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/media")
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = mediaService.uploadFile(file);
        return ResponseEntity.ok(fileName);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileName) {
        mediaService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }
} 