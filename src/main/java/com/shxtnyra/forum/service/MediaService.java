package com.shxtnyra.forum.service;

import com.shxtnyra.forum.config.MediaConfig;
import com.shxtnyra.forum.exception.MediaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaService {
    private final MediaConfig mediaConfig;

    public String uploadFile(MultipartFile file) {
        validateFile(file);
        
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path filePath = Paths.get(mediaConfig.getUploadDir(), fileName);
        
        try {
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath);
            return fileName;
        } catch (IOException e) {
            throw new MediaException("Failed to upload file: " + e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        Path filePath = Paths.get(mediaConfig.getUploadDir(), fileName);
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new MediaException("Failed to delete file: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new MediaException("File is empty");
        }

        if (file.getSize() > mediaConfig.getMaxFileSize()) {
            throw new MediaException("File size exceeds maximum allowed size");
        }

        String contentType = file.getContentType();
        if (contentType == null || !Arrays.asList(mediaConfig.getAllowedTypes()).contains(contentType)) {
            throw new MediaException("File type not allowed");
        }
    }

    private String generateUniqueFileName(String originalFileName) {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }
} 