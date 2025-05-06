package com.shxtnyra.forum.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Пока так будет надо разобраться как хранить
    private String content;

    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    // Комментарий (родитель), под которым был написан новый комментарий (ребенок)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CommentEntity parent;

    // Ответы на комментарий
    @OneToMany(mappedBy = "parent", orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CommentEntity> replies = new ArrayList<>();

    // Жалобы
    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY)
    private List<ReportEntity> reports = new ArrayList<>();

    // Реакции (пока что только лайк/дизлайк)
    @Column(nullable = false, columnDefinition = "integer default 0")
    private int likes = 0;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private int dislikes = 0;

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
