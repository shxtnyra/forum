package com.shxtnyra.forum.entity;

import com.shxtnyra.forum.enums.ReportReason;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    // Может быть null, если жалоба на комментарий
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private PostEntity post;

    // Может быть null, если жалоба на пост
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private CommentEntity comment;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean solved = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(nullable = false)
    private LocalDateTime reportedAt;

    // Автоматическое проставление дат
    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
    }
}
