package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.PostRatingEntity;
import com.shxtnyra.forum.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PostRatingRepository extends JpaRepository<PostRatingEntity, Long> {
    Optional<PostRatingEntity> findByPostIdAndUser(Long postId, UserEntity user);

    @Query("SELECT r FROM PostRatingEntity r WHERE r.post.id = :postId AND r.user.id = :userId")
    Optional<PostRatingEntity> findByPostIdAndUserId(@Param("postId") Long postId,
                                                     @Param("userId") Long userId);

    double countByUserAndCreatedAtAfter(UserEntity user, LocalDateTime dateTime);
}
