package com.shxtnyra.forum.entity;

import com.shxtnyra.forum.enums.PostStatus;
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

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String previewContent;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.DRAFT;

    @Column(length = 500)
    private String coverImageUrl;

    @ManyToMany
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<TagEntity> tags = new HashSet<>();

    @Column(columnDefinition = "boolean default false")
    private boolean isPinned = false;

    @Column(columnDefinition = "boolean default false")
    private boolean isPromoted = false;

    @Column(columnDefinition = "boolean default false")
    private boolean isEditorChoice = false;

    @Column(length = 200)
    private String metaDescription;

    @Column(length = 200)
    private String metaKeywords;

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
