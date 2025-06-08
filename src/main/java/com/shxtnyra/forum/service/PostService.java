package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.TopicEntity;
import com.shxtnyra.forum.entity.UserEntity;
import com.shxtnyra.forum.enums.Role;
import com.shxtnyra.forum.exception.exceptions.AccessDeniedException;
import com.shxtnyra.forum.exception.exceptions.EntityNotFoundException;
import com.shxtnyra.forum.mapper.PostMapper;
import com.shxtnyra.forum.repository.PostRepository;
import com.shxtnyra.forum.repository.TopicRepository;
import com.shxtnyra.forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public void editDraft(Long postId, PostCreateDTO createDTO, UserEntity user) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (!post.isDraft()) {
            throw new IllegalStateException("Post already released");
        }

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        if (post.isDeleted()) {
            throw new AccessDeniedException("Access denied");
        }

        TopicEntity topic = topicRepository.findById(createDTO.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Такая тема не найдена"));

        post.setTitle(createDTO.getTitle());
        post.setContent(createDTO.getContent());
        post.setTopic(topic);
    }

    // получение поста
    @Transactional
    public PostDetailsDTO getPostById(Long id, UserEntity user) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        if (post.isInvisible()) {
            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Access denied");
            }
            // Возвращаем без увеличения просмотров
            return PostMapper.toDetailsDTO(post);
        }

        if (post.isDeleted()) {
            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Access denied");
            }
            // Возвращаем без увеличения просмотров
            return PostMapper.toDetailsDTO(post);
        }

        // Если пост еще черновик
        if (post.isDraft()) {
            // Черновики доступны только автору и админам
            if (user.getRole() == Role.ROLE_MODERATOR) {
                throw new AccessDeniedException("Access denied");
            }

            if (!user.getId().equals(post.getAuthor().getId())) {
                throw new AccessDeniedException("Access denied");
            }
            // Возвращаем без увеличения просмотров
            return PostMapper.toDetailsDTO(post);
        }

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
        if (topicId != null) {
            if ("top".equals(sort)) {
                posts = postRepository.findTopPostsByPeriodAndTopic(lastSeenId, periodStart, topicId, limit);
            } else {
                posts = postRepository.findNewestPostsByTopic(lastSeenId, topicId, limit);
            }
        } else {
            if ("top".equals(sort)) {
                posts = postRepository.findTopPostsByPeriod(lastSeenId, periodStart, limit);
            } else {
                posts = postRepository.findNewestPosts(lastSeenId, limit);
            }
        }

        return posts.map(PostMapper::toShortDTO);
    }

    // Получить все скрытые посты
    public Page<PostShortDTO> getInvisiblePosts(Pageable pageable) {
        return postRepository.getInvisiblePosts(pageable).map(PostMapper::toShortDTO);
    }

    // Получить посты одного пользователя (простые посты либо вместе со скрытыми)
    public Page<PostShortDTO> getPostsByUserByVisibility(Long userId, boolean includeInvisible, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        return postRepository.getPostsByUserByVisibility(userId, includeInvisible, pageable).map(PostMapper::toShortDTO);
    }

    // Изменение видимости поста
    @Transactional
    public void changeVisibility(Long id, boolean isInvisible) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Черновикам нельзя менять видимость
        if (post.isDraft()) {
            throw new IllegalStateException("Access denied");
        }

        post.setInvisible(isInvisible);
    }

    // Получить удаленные посты определенного автора
    @Transactional
    public Page<PostShortDTO> getDeletedPostsByUser(Long authorId, Pageable pageable) {
        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException("User not found");
        }

        return postRepository.getDeletedPostsByUser(authorId, pageable).map(PostMapper::toShortDTO);
    }

    // Мягкое удаление
    @Transactional
    public void deletePost(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Если уже удален
        if (post.isDeleted()) {
            return;
        }

        post.setDeleted(true);
    }

    // Восстановление поста
    @Transactional
    public void recoverPost(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Если уже восстановлен
        if (!post.isDeleted()) {
            return;
        }

        post.setDeleted(false);
    }

    // Выложить пост
    @Transactional
    public PostDetailsDTO releasePost(Long id, UserEntity user) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // Выложить может только автор
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        // Нельзя выложить удаленное
        if (post.isDeleted()) {
            throw new AccessDeniedException("Access denied");
        }

        // Если уже выложен
        if (!post.isDraft()) {
            return PostMapper.toDetailsDTO(post);
        }

        post.setCreatedAt(LocalDateTime.now());
        post.setDraft(false);

        return PostMapper.toDetailsDTO(post);
    }

    // Получить свои черновики
    @Transactional
    public Page<PostShortDTO> getDrafts(UserEntity user, Pageable pageable) {
        return postRepository.getDraftPosts(user.getId(), pageable).map(PostMapper::toShortDTO);
    }

    // Получить чужие черновики (для админов)
    public Page<PostShortDTO> getDraftsByUserId(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }

        return postRepository.getDraftsByUserId(userId, pageable).map(PostMapper::toShortDTO);
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
