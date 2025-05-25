package com.shxtnyra.forum.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "media")
public class MediaConfig {
    private String uploadDir;
    private long maxFileSize;
    private String[] allowedTypes;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public String[] getAllowedTypes() {
        return allowedTypes;
    }

    public void setAllowedTypes(String[] allowedTypes) {
        this.allowedTypes = allowedTypes;
    }
} 