package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.PostRatingEntity;
import com.shxtnyra.forum.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRatingRepository extends JpaRepository<PostRatingEntity, Long> {
    Optional<PostRatingEntity> findByPostAndUser(PostEntity post, UserEntity user);
    void deleteByPostIdAndUser(PostEntity post, UserEntity user);
    Optional<PostRatingEntity> findByPostIdAndUser(Long postId, UserEntity user);
}
