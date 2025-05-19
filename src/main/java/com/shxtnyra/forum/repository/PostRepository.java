package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    // 1. Топ постов за период (все темы)
    @Query("SELECT p FROM PostEntity p " +
            "WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId) " +
            "AND p.createdAt >= :periodStart " +
            "ORDER BY (p.likeCount + p.dislikeCount) DESC, p.id DESC")
    Slice<PostEntity> findTopPostsByPeriod(
            @Param("lastSeenId") Long lastSeenId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("limit") int limit
    );

    // 2. Топ постов за период по теме (по topicId)
    @Query("SELECT p FROM PostEntity p " +
            "WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId) " +
            "AND p.topic.id = :topicId " +  // Используем ID темы
            "AND p.createdAt >= :periodStart " +
            "ORDER BY (p.likeCount + p.dislikeCount) DESC, p.id DESC")
    Slice<PostEntity> findTopPostsByPeriodAndTopic(
            @Param("lastSeenId") Long lastSeenId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("topicId") Long topicId,
            @Param("limit") int limit
    );

    // 3. Свежие посты (все темы)
    @Query("SELECT p FROM PostEntity p " +
            "WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId) " +
            "ORDER BY p.id DESC")
    Slice<PostEntity> findNewestPosts(
            @Param("lastSeenId") Long lastSeenId,
            @Param("limit") int limit
    );

    // 4. Свежие посты по теме (по topicId)
    @Query("SELECT p FROM PostEntity p " +
            "WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId) " +
            "AND p.topic.id = :topicId " +
            "ORDER BY p.id DESC")
    Slice<PostEntity> findNewestPostsByTopic(
            @Param("lastSeenId") Long lastSeenId,
            @Param("topicId") Long topicId,
            @Param("limit") int limit
    );

    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @Query("SELECT p.author.id FROM PostEntity p WHERE p.id = :id")
    Optional<Long> findAuthorIdById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount + :increment WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId, @Param("increment") int increment);

    @Modifying
    @Query("UPDATE PostEntity p SET p.dislikeCount = p.dislikeCount + :increment WHERE p.id = :postId")
    void incrementDislikeCount(@Param("postId") Long postId, @Param("increment") int increment);

    @Modifying
    @Query("UPDATE PostEntity p SET " +
            "p.likeCount = p.likeCount + :likeDelta, " +
            "p.dislikeCount = p.dislikeCount + :dislikeDelta " +
            "WHERE p.id = :postId")
    void updateRatingCounters(@Param("postId") Long postId,
                              @Param("likeDelta") int likeDelta,
                              @Param("dislikeDelta") int dislikeDelta);
}
