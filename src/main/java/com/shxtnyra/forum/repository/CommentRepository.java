package com.shxtnyra.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.shxtnyra.forum.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    /**
     * Проекция для получения ID поста и уровня вложенности комментария.
     * Используется в CommentService.createComment().
     */
    interface ParentInfo {
        Long getPostId();
        int getLevel();
    }

    @Query("SELECT c.post.id as postId, c.level as level FROM CommentEntity c WHERE c.id = :commentId")
    Optional<ParentInfo> findParentInfoById(@Param("commentId") Long commentId);

    // Все комментарии к посту
    List<CommentEntity> findByPostId(Long postId);

    // Комментарии пользователя
    Page<CommentEntity> findByAuthorId(Long authorId, Pageable pageable);

    @Query("SELECT c.author.id FROM CommentEntity c WHERE c.id = :id")
    Optional<Long> findAuthorIdById(@Param("id") Long id);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.likeCount = c.likeCount + :increment WHERE c.id = :commentId")
    void incrementLikeCount(@Param("commentId") Long commentId, @Param("increment") int increment);

    @Modifying
    @Query("UPDATE CommentEntity c SET c.dislikeCount = c.dislikeCount + :increment WHERE c.id = :commentId")
    void incrementDislikeCount(@Param("commentId") Long commentId, @Param("increment") int increment);

    @Modifying
    @Query("UPDATE CommentEntity c SET " +
            "c.likeCount = c.likeCount + :likeDelta, " +
            "c.dislikeCount = c.dislikeCount + :dislikeDelta " +
            "WHERE c.id = :postId")
    void updateRatingCounters(@Param("commentId") Long commentId,
                              @Param("likeDelta") int likeDelta,
                              @Param("dislikeDelta") int dislikeDelta);
}
