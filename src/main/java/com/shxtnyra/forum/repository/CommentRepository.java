package com.shxtnyra.forum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.shxtnyra.forum.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
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

    // Все ответы на комментарий
    List<CommentEntity> findByParentId(Long parentId);

    // Комментарии пользователя
    Page<CommentEntity> findByAuthorId(Long authorId, Pageable pageable);
}
