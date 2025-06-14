package com.shxtnyra.forum.service;

import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.PostRatingEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.repository.PostRatingRepository;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostRatingService {
    private final PostRatingRepository postRatingRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private static final double POST_RATING_VALUE = 1.0;
    private static final double POST_RATING_DOUBLE_VALUE = POST_RATING_VALUE * 2;


    @Transactional
    public void ratePost(Long postId, UserEntity user, boolean isLike) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        Long authorId = post.getAuthor().getId();

        if (post.isInvisible() || post.isDraft() || post.isDeleted()) {
            throw new IllegalArgumentException("Нельзя оценить этот пост");
        }

        postRatingRepository.findByPostIdAndUserId(postId, user.getId()).ifPresentOrElse(
                rating -> handleExistingRating(postId, authorId, rating, isLike),
                () -> createNewRating(postId, authorId, user, isLike)
        );
    }

    private void handleExistingRating(Long postId, Long authorId,
                                      PostRatingEntity rating, boolean newIsLike) {
        if (rating.isLike() == newIsLike) {
            removeRating(postId, authorId, rating);
        } else {
            updateRating(postId, authorId, rating, newIsLike);
        }
    }

    private void createNewRating(Long postId, Long authorId, UserEntity user, boolean isLike) {
        postRatingRepository.save(
                PostRatingEntity.builder()
                        .post(PostEntity.builder().id(postId).build())
                        .user(user)
                        .isLike(isLike)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        if (isLike) {
            postRepository.incrementLikeCount(postId, 1);
            userRepository.incrementTotalRating(authorId, POST_RATING_VALUE);
        } else {
            postRepository.incrementDislikeCount(postId, 1);
            userRepository.incrementTotalRating(authorId, -POST_RATING_VALUE);
        }
    }

    private void updateRating(Long postId, Long authorId,
                              PostRatingEntity rating, boolean newIsLike) {
        if (newIsLike) {
            postRepository.updateRatingCounters(postId, 1, -1);
            userRepository.incrementTotalRating(authorId, POST_RATING_DOUBLE_VALUE);
        } else {
            postRepository.updateRatingCounters(postId, -1, 1);
            userRepository.incrementTotalRating(authorId, -POST_RATING_DOUBLE_VALUE);
        }

        // Установка новой оценки
        rating.setLike(newIsLike);
    }

    private void removeRating(Long postId, Long authorId, PostRatingEntity rating) {
        if (rating.isLike()) {
            postRepository.incrementLikeCount(postId, -1);
            userRepository.incrementTotalRating(authorId, -POST_RATING_VALUE);
        } else {
            postRepository.incrementDislikeCount(postId, -1);
            userRepository.incrementTotalRating(authorId, POST_RATING_VALUE);
        }
        postRatingRepository.delete(rating);
    }
}
