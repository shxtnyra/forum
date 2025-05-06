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
    @JoinColumn(name = "reporter_id", nullable = false)
    private UserEntity reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentEntity comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(name = "reported_at", updatable = false, nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "is_solved", nullable = false, columnDefinition = "boolean default false")
    private boolean isSolved = false;

    // Автоматическое проставление дат
    @PrePersist
    protected void onCreate() {
        reportedAt = LocalDateTime.now();
    }

}
