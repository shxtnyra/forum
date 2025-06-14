package com.shxtnyra.forum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    // Пока так будет надо разобраться как хранить
    private String content;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @ManyToOne
    @JoinColumn(nullable = false, name = "topic_id")
    private TopicEntity topic;

    @Column(nullable = false, columnDefinition = "integer default 0 CHECK (view_count >= 0)")
    private int viewCount = 0;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likeCount = 0;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dislikeCount = 0;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<PostRatingEntity> ratings = new HashSet<>();

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean invisible = false;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deleted = false;

    @Builder.Default
    @Column(columnDefinition = "boolean default true")
    private boolean draft = true;

    // Автоматическое проставление дат
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
