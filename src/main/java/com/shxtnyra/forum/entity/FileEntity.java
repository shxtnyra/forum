package com.shxtnyra.forum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileType;

    @Lob
    @Column(nullable = false)
    private byte[] data;

    @Column(name = "uploaded_at", updatable = false, nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
    }
}

