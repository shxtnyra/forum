package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    Page<PostEntity> findByAuthorId(Long authorId, Pageable pageable);

    // Посты за сегодня
    @Query("SELECT p FROM PostEntity p WHERE p.createdAt >= :startOfDay ORDER BY p.createdAt DESC")
    Page<PostEntity> findTodayPosts(
            @Param("startOfDay") LocalDateTime startOfDay,
            Pageable pageable);

    @Query("""
        SELECT p FROM PostEntity p
        WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId)
        ORDER BY p.createdAt DESC, p.id DESC
        """)
    Slice<PostEntity> findForNewsFeed(
            @Param("lastSeenId") Long lastSeenId,
            Pageable pageable);

    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    @EntityGraph(attributePaths = {"topic", "author"})
    @Query("""
        SELECT p FROM PostEntity p 
        JOIN p.topic t 
        WHERE t.slug = :topicSlug
        ORDER BY p.createdAt DESC
        """)
    Page<PostEntity> findByTopicSlug(
            @Param("topicSlug") String topicSlug,
            Pageable pageable
    );
}
