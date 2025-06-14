package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.CommentEntity;
import com.shxtnyra.forum.entity.CommentRatingEntity;
import com.shxtnyra.forum.entity.PostRatingEntity;
import com.shxtnyra.forum.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CommentRatingRepository extends JpaRepository<CommentRatingEntity, Long> {
    @Query("SELECT r FROM CommentRatingEntity r WHERE r.comment.id = :commentId AND r.user.id = :userId")
    Optional<CommentRatingEntity> findByCommentIdAndUserId(@Param("commentId") Long commentId,
                                                     @Param("userId") Long userId);

    double countByUserAndCreatedAtAfter(UserEntity user, LocalDateTime dateTime);
}
