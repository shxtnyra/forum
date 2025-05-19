package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.user.UserDetailsDTO;
import com.shxtnyra.forum.mapper.UserMapper;
import com.shxtnyra.forum.repository.CommentRatingRepository;
import com.shxtnyra.forum.repository.PostRatingRepository;
import com.shxtnyra.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRatingService {
    private final UserRepository userRepository;
    private final PostRatingRepository postRatingRepository;
    private final CommentRatingRepository commentRatingRepository;

    //TODO возможно появится
    //@Scheduled(cron = "0 0 3 * * ?") // Каждый день в 3 ночи
    @Scheduled(fixedRate = 30_000) // Каждые 30 секунд для теста
    @Transactional
    public void updateWeeklyRatings() {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        userRepository.findAll().forEach(user -> {
            double postRating = postRatingRepository.countByUserAndCreatedAtAfter(user, weekAgo);
            double commentRating = commentRatingRepository.countByUserAndCreatedAtAfter(user, weekAgo) * 0.4;

            user.setWeeklyRating(postRating + commentRating);
        });
    }
}
