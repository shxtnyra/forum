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
    public PostDetailsDTO editDraft(Long postId, PostCreateDTO createDTO, UserEntity user) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        if (!post.isDraft()) {
            throw new IllegalStateException("Нельзя редактировать этот пост");
        }

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Нельзя редактировать этот пост");
        }

        if (post.isDeleted()) {
            throw new AccessDeniedException("Нельзя редактировать этот пост");
        }

        TopicEntity topic = topicRepository.findById(createDTO.getTopicId())
                .orElseThrow(() -> new EntityNotFoundException("Такая тема не найдена"));

        post.setTitle(createDTO.getTitle());
        post.setContent(createDTO.getContent());
        post.setTopic(topic);
        post.setUpdatedAt(LocalDateTime.now());

        return PostMapper.toDetailsDTO(post);
    }

    // получение поста
    @Transactional
    public PostDetailsDTO getPostById(Long id, UserEntity user) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        if (post.isInvisible()) {
            // Если пользователь не авторизован, то нельзя получить скрытый пост
            if (user == null)
                throw new AccessDeniedException("Нельзя получить этот пост");

            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Нельзя получить этот пост");
            }

            // Возвращаем без увеличения просмотров
            return PostMapper.toDetailsDTO(post);
        }

        if (post.isDeleted()) {
            if (user == null)
                throw new AccessDeniedException("Нельзя получить этот пост");

            if (user.getRole() == Role.ROLE_USER) {
                throw new AccessDeniedException("Нельзя получить этот пост");
            }

            // Возвращаем без увеличения просмотров
            return PostMapper.toDetailsDTO(post);
        }

        // Если пост еще черновик
        if (post.isDraft()) {
            // Если пользователь не авторизован, то нельзя получить черновик
            if (user == null)
                throw new AccessDeniedException("Нельзя получить этот пост");

            // Если пользователь это не автор поста
            if (!user.getId().equals(post.getAuthor().getId())) {
            // Если пользователь не админ или модератор 
                if (user.getRole() == Role.ROLE_USER) 
                    throw new AccessDeniedException("Нельзя получить этот пост");
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
            throw new IllegalArgumentException("Такая тема не найдена");
        }

        // Определяем период
        LocalDateTime periodStart = null;
        if (period != null) {
            periodStart = switch (period) {
                case "day" -> LocalDateTime.now().minusDays(1);
                case "week" -> LocalDateTime.now().minusWeeks(1);
                case "month" -> LocalDateTime.now().minusMonths(1);
                case "year" -> LocalDateTime.now().minusYears(1);
                case "all" -> LocalDateTime.of(2000, 1, 1, 0, 0);
                default -> throw new IllegalArgumentException("Неверный период");
            };
        }

        Long topicId = null;
        if (topicSlug != null) {
            topicId = topicRepository.findIdBySlug(topicSlug);
            if (topicId == null) {
                throw new IllegalArgumentException("Такая тема не найдена");
            }
        }

        // Выбираем подходящий метод репозитория в зависимости от параметров
        Slice<PostEntity> posts;
        if (topicId != null) {
            if ("top".equals(sort)) {
                posts = postRepository.findTopPostsByPeriodAndTopic(lastSeenId, periodStart, topicId, limit);
            } else {
                posts = postRepository.findNewestPostsByTopic(lastSeenId, periodStart, topicId, limit);
            }
        } else {
            if ("top".equals(sort)) {
                posts = postRepository.findTopPostsByPeriod(lastSeenId, periodStart, limit);
            } else {
                posts = postRepository.findNewestPosts(lastSeenId, periodStart, limit);
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
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return postRepository.getPostsByUserByVisibility(userId, includeInvisible, pageable).map(PostMapper::toShortDTO);
    }

    // Изменение видимости поста
    @Transactional
    public void changeVisibility(Long id, boolean isInvisible) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        // Черновикам нельзя менять видимость
        if (post.isDraft()) {
            throw new IllegalStateException("Нельзя изменить этот пост");
        }

        post.setInvisible(isInvisible);
    }

    // Получить удаленные посты определенного автора
    @Transactional
    public Page<PostShortDTO> getDeletedPostsByUser(Long authorId, Pageable pageable) {
        if (!userRepository.existsById(authorId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return postRepository.getDeletedPostsByUser(authorId, pageable).map(PostMapper::toShortDTO);
    }

    // Мягкое удаление
    @Transactional
    public void deletePost(Long id) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

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
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

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
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        // Выложить может только автор
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Нельзя выложить этот пост");
        }

        // Нельзя выложить удаленное
        if (post.isDeleted()) {
            throw new AccessDeniedException("Нельзя выложить этот пост");
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
            throw new EntityNotFoundException("Пользователь не найден");
        }

        return postRepository.getDraftsByUserId(userId, pageable).map(PostMapper::toShortDTO);
    }

    // Обновление пока put, надо ещё path добавить будет
    @Transactional
    public PostDetailsDTO updatePost(Long id, PostCreateDTO dto) {
        PostEntity post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return PostMapper.toDetailsDTO(post);
    }
}
