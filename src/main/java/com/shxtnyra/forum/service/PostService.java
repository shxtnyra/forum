package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.TopicEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.PostMapper;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;

    @Transactional
    public PostDetailsDTO createPost(PostCreateDTO createDTO, UserEntity user) {

        TopicEntity topic = topicRepository.findById(createDTO.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Такая тема не найдена"));

        PostEntity post = PostEntity.builder()
                .title(createDTO.getTitle())
                .content(createDTO.getContent())
                .topic(topic)
                .author(user)
                .build();

        post = postRepository.save(post);
        return PostMapper.toDetailsDTO(post);
    }

    // получение поста
    @Transactional
    public PostDetailsDTO getPostById(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Увеличиваем число просмотров
        postRepository.incrementViewCount(id);
        return PostMapper.toDetailsDTO(post);
    }

    public Slice<PostShortDTO> getNewsFeed(Long lastSeenId, String topicSlug,
                                           String period, String sort,
                                           int limit) {

        // Валидация темы
        if (topicSlug != null && !topicRepository.existsBySlug(topicSlug)) {
            throw new IllegalArgumentException("Topic not found");
        }

        // Определяем период
        LocalDateTime periodStart = null;
        if (period != null) {
            periodStart = switch (period) {
                case "day" -> LocalDateTime.now().minusDays(1);
                case "week" -> LocalDateTime.now().minusWeeks(1);
                case "month" -> LocalDateTime.now().minusMonths(1);
                default -> throw new IllegalArgumentException("Invalid period");
            };
        }

        Long topicId = null;
        if (topicSlug != null) {
            topicId = topicRepository.findIdBySlug(topicSlug);
            if (topicId == null) {
                throw new IllegalArgumentException("Topic not found");
            }
        }

        // Выбираем подходящий метод репозитория в зависимости от параметров
        Slice<PostEntity> posts;
        if (topicId != null){
            if ("top".equals(sort)){
                posts = postRepository.findTopPostsByPeriodAndTopic(lastSeenId, periodStart, topicId, limit);
            }else {
                posts = postRepository.findNewestPostsByTopic(lastSeenId, topicId, limit);
            }
        }else {
            if ("top".equals(sort)){
                posts = postRepository.findTopPostsByPeriod(lastSeenId, periodStart, limit);
            }
            else {
                posts = postRepository.findNewestPosts(lastSeenId, limit);
            }
        }

        return posts.map(PostMapper::toShortDTO);
    }

    // Обновление пока put, надо ещё path добавить будет
    @Transactional
    public PostDetailsDTO updatePost(Long id, PostCreateDTO dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return PostMapper.toDetailsDTO(post);
    }
}
