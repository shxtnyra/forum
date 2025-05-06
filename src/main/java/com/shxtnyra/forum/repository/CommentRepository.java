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

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // Все комментарии к посту
    Page<CommentEntity> findByPostId(Long postId, Pageable pageable);

    // Поиск по тексту
    @Query("SELECT c FROM CommentEntity c WHERE LOWER(c.content) LIKE LOWER(CONCAT('%', :content, '%'))")
    List<CommentEntity> findByTextContainingIgnoreCase(@Param("text") String text);

    // Комментарии пользователя
    Page<CommentEntity> findByAuthorId(Long authorId, Pageable pageable);

    // Все ответы на комментарий
    Page<CommentEntity> findByParentId(Long parentId, Pageable pageable);

    // Увеличить счётчик лайков
    @Modifying
    @Query("UPDATE CommentEntity c SET c.likes = c.likes + 1 WHERE c.id = :id")
    void incrementLikes(@Param("id") Long id);

    // Увеличить счётчик дизлайков
    @Modifying
    @Query("UPDATE CommentEntity c SET c.dislikes = c.dislikes + 1 WHERE c.id = :id")
    void incrementDislikes(@Param("id") Long id);
}
