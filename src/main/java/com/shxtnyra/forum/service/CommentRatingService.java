package com.shxtnyra.forum.service;

import com.shxtnyra.forum.entity.*;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.repository.CommentRatingRepository;
import com.shxtnyra.forum.repository.CommentRepository;
import com.shxtnyra.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CommentRatingService {
    private final CommentRatingRepository commentRatingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private static final double COMMENT_RATING_VALUE = 0.2;
    private static final double COMMENT_RATING_DOUBLE_VALUE = COMMENT_RATING_VALUE * 2;


    @Transactional
    public void rateComment(Long commentId, UserEntity user, boolean isLike){
        Long authorId = commentRepository.findAuthorIdById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Такой комментарий не найден"));

        commentRatingRepository.findByCommentIdAndUserId(commentId, user.getId())
                .ifPresentOrElse(
                        rating -> handleExistingRating(commentId, authorId, rating, isLike),
                        () -> createNewRating(commentId, authorId, user, isLike)
                );
    }

    private void handleExistingRating(Long commentId, Long authorId,
                                      CommentRatingEntity rating, boolean newIsLike){
        if (rating.isLike() == newIsLike){
            removeRating(commentId, authorId, rating);
        }else {
            updateRating(commentId, authorId, rating, newIsLike);
        }
    }

    private void createNewRating(Long commentId, Long authorId, UserEntity user, boolean isLike){
        commentRatingRepository.save(
            CommentRatingEntity.builder()
                    .comment(CommentEntity.builder().id(commentId).build())
                    .user(user)
                    .isLike(isLike)
                    .createdAt(LocalDateTime.now())
                    .build()
        );

        if (isLike) {
            commentRepository.incrementLikeCount(commentId, 1);
            userRepository.incrementTotalRating(authorId, COMMENT_RATING_VALUE);
        } else {
            commentRepository.incrementDislikeCount(commentId, 1);
            userRepository.incrementTotalRating(authorId, -COMMENT_RATING_VALUE);
        }
    }

    private void updateRating(Long commentId, Long authorId,
                              CommentRatingEntity rating, boolean newIsLike) {
        if (newIsLike){
            commentRepository.updateRatingCounters(commentId, 1, -1);
            userRepository.incrementTotalRating(authorId, COMMENT_RATING_DOUBLE_VALUE);
        }else {
            commentRepository.updateRatingCounters(commentId, -1, 1);
            userRepository.incrementTotalRating(authorId, -COMMENT_RATING_DOUBLE_VALUE);
        }

        rating.setLike(newIsLike);
    }

    private void removeRating(Long commentId, Long authorId, CommentRatingEntity rating){
        if (rating.isLike()){
            commentRepository.incrementLikeCount(commentId, -1);
            userRepository.incrementTotalRating(authorId, -COMMENT_RATING_VALUE);
        }else {
            commentRepository.incrementDislikeCount(commentId, -1);
            userRepository.incrementTotalRating(authorId, COMMENT_RATING_VALUE);
        }

        commentRatingRepository.delete(rating);
    }
}
