package com.shxtnyra.forum.service;

import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.PostRatingEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.repository.PostRatingRepository;
import com.shxtnyra.forum.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostRatingService {
    private final PostRatingRepository postRatingRepository;
    private final PostRepository postRepository;

    @Transactional
    public void ratePost(Long postId, UserEntity user, boolean isLike) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        Optional<PostRatingEntity> existingRating = postRatingRepository.findByPostAndUser(post, user);

        if (existingRating.isPresent()){
            if (existingRating.get().isLike() == isLike){
                removeRating(post, existingRating.get());
            }else {
                updateRating(post, existingRating.get(), isLike);
            }

        }else {
            addNewRating(post, user, isLike);
        }
    }

    private void addNewRating(PostEntity post, UserEntity user, boolean isLike) {
        PostRatingEntity rating = PostRatingEntity.builder()
                .post(post)
                .user(user)
                .isLike(isLike)
                .build();
        postRatingRepository.save(rating);

        // так как транзакция изменения сохранятся
        if (isLike) {
            post.setLikeCount(post.getLikeCount() + 1);
        } else {
            post.setDislikeCount(post.getDislikeCount() + 1);
        }
    }

    private void updateRating(PostEntity post, PostRatingEntity rating, boolean newIsLike) {
        // Уменьшаем старый счетчик
        if (rating.isLike()) {
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            post.setDislikeCount(post.getDislikeCount() - 1);
        }

        // Меняем оценку
        rating.setLike(newIsLike);

        // Увеличиваем новый счетчик
        if (newIsLike) {
            post.setLikeCount(post.getLikeCount() + 1);
        } else {
            post.setDislikeCount(post.getDislikeCount() + 1);
        }
    }

    private void removeRating(PostEntity post, PostRatingEntity rating) {
        if (rating.isLike()) {
            post.setLikeCount(post.getLikeCount() - 1);
        } else {
            post.setDislikeCount(post.getDislikeCount() - 1);
        }

        postRatingRepository.delete(rating);
    }

    public Optional<Boolean> getUserRating(Long postId, UserEntity user) {
        return postRatingRepository.findByPostIdAndUser(postId, user)
                .map(PostRatingEntity::isLike);
    }
}
