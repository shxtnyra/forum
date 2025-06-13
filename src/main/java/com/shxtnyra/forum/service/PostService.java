package com.shxtnyra.forum.service;

import com.shxtnyra.forum.dto.post.PostCreateDTO;
import com.shxtnyra.forum.dto.post.PostDetailsDTO;
import com.shxtnyra.forum.dto.post.PostShortDTO;
import com.shxtnyra.forum.entity.PostEntity;
import com.shxtnyra.forum.entity.TopicEntity;
import com.shxtnyra.forum.entity.UserEntity;
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
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Сервис для работы с постами.
 * Предоставляет методы для создания, обновления, удаления и получения постов,
 * а также для работы с черновиками и статистикой.
 */
@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    /**
     * Создает новый пост или черновик.
     * Преобразует DTO в сущность и сохраняет её в базе данных.
     *
     * @param createDTO данные для создания поста
     * @param user автор поста
     * @return DTO созданного поста
     * @throws EntityNotFoundException если тема не найдена
     */
    @Transactional
    public PostDetailsDTO createPost(PostCreateDTO createDTO, UserEntity user) {
        TopicEntity topic = findTopicOrThrow(createDTO.getTopicId());
        
        PostEntity post = PostEntity.builder()
                .title(createDTO.getTitle())
                .contentBlocks(mapContentBlocks(createDTO.getBlocks()))
                .topic(topic)
                .author(user)
                .draft(createDTO.draft())
                .build();

        return PostMapper.toDetailsDTO(postRepository.save(post));
    }

    /**
     * Создает новый черновик.
     * Устанавливает флаг draft в true и вызывает createPost.
     *
     * @param createDTO данные для создания черновика
     * @param user автор черновика
     * @return DTO созданного черновика
     */
    @Transactional
    public PostDetailsDTO createDraft(PostCreateDTO createDTO, UserEntity user) {
        PostCreateDTO draftDTO = new PostCreateDTO(
                createDTO.title(),
                createDTO.blocks(),
                createDTO.topicId(),
                true
        );
        return createPost(draftDTO, user);
    }

    /**
     * Обновляет существующий пост.
     * Проверяет права доступа и обновляет содержимое поста.
     *
     * @param id ID поста
     * @param dto новые данные
     * @param user пользователь, выполняющий обновление
     * @return DTO обновленного поста
     * @throws EntityNotFoundException если пост не найден
     * @throws AccessDeniedException если у пользователя нет прав на обновление
     */
    @Transactional
    public PostDetailsDTO updatePost(Long id, PostCreateDTO dto, UserEntity user) {
        PostEntity post = findPostOrThrow(id);
        
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        post.setTitle(dto.getTitle());
        post.setContentBlocks(mapContentBlocks(dto.getBlocks()));
        
        if (dto.topicId() != null && !dto.topicId().equals(post.getTopic().getId())) {
            TopicEntity topic = findTopicOrThrow(dto.topicId());
            post.setTopic(topic);
        }

        return PostMapper.toDetailsDTO(post);
    }

    /**
     * Публикует черновик.
     * Устанавливает флаг draft в false и обновляет время создания.
     * Публиковать может только автор поста.
     *
     * @param postId ID черновика
     * @param user пользователь, выполняющий публикацию
     * @return DTO опубликованного поста
     * @throws EntityNotFoundException если черновик не найден
     * @throws AccessDeniedException если у пользователя нет прав на публикацию
     */
    @Transactional
    public PostDetailsDTO publishDraft(Long postId, UserEntity user) {
        PostEntity post = postRepository.findDraftByIdAndAuthor(postId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Черновик не найден"));

        if (!post.isDraft()) {
            return PostMapper.toDetailsDTO(post);
        }

        post.setDraft(false);
        post.setCreatedAt(LocalDateTime.now());
        
        return PostMapper.toDetailsDTO(post);
    }

    /**
     * Мягко удаляет пост.
     * Устанавливает флаг deleted в true.
     *
     * @param id ID поста
     * @param user пользователь, выполняющий удаление
     * @throws EntityNotFoundException если пост не найден
     * @throws AccessDeniedException если у пользователя нет прав на удаление
     */
    @Transactional
    public void deletePost(Long id, UserEntity user) {
        PostEntity post = findPostOrThrow(id);
        
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        post.setDeleted(true);
    }

    /**
     * Восстанавливает удаленный пост.
     * Устанавливает флаг deleted в false.
     *
     * @param id ID поста
     * @param user пользователь, выполняющий восстановление
     * @throws EntityNotFoundException если пост не найден
     * @throws AccessDeniedException если у пользователя нет прав на восстановление
     */
    @Transactional
    public void recoverPost(Long id, UserEntity user) {
        PostEntity post = findPostOrThrow(id);
        
        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        post.setDeleted(false);
    }

    /**
     * Получает пост по ID.
     * Учитывает права доступа и увеличивает счетчик просмотров.
     *
     * @param id ID поста
     * @param currentUserId ID текущего пользователя
     * @return DTO поста
     * @throws EntityNotFoundException если пост не найден
     * @throws AccessDeniedException если у пользователя нет прав на просмотр
     */
    @Transactional
    public PostDetailsDTO getPost(Long id, Long currentUserId) {
        PostEntity post = postRepository.findByIdAndVisibility(id, currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));

        if (!post.isDraft()) {
            postRepository.incrementViewCount(id);
        }

        return PostMapper.toDetailsDTO(post);
    }

    /**
     * Получает ленту постов.
     * Поддерживает фильтрацию по теме, периоду и сортировку.
     *
     * @param lastSeenId ID последнего просмотренного поста
     * @param topicId ID темы для фильтрации
     * @param period период времени
     * @param sort тип сортировки
     * @param limit максимальное количество постов
     * @param includeDrafts включать ли черновики
     * @return срез постов
     */
    public Slice<PostShortDTO> getFeed(
            Long lastSeenId,
            Long topicId,
            String period,
            String sort,
            int limit,
            boolean includeDrafts) {
        
        LocalDateTime periodStart = parsePeriod(period);
        Slice<PostEntity> posts = findPostsByCriteria(lastSeenId, topicId, periodStart, sort, limit);
        
        return posts.map(PostMapper::toShortDTO);
    }

    /**
     * Ищет посты по заголовку.
     *
     * @param query поисковый запрос
     * @param topicId ID темы для фильтрации
     * @param pageable параметры пагинации
     * @return страница найденных постов
     */
    public Page<PostShortDTO> searchPosts(String query, Long topicId, Pageable pageable) {
        return postRepository.searchPosts(query, topicId, pageable)
                .map(PostMapper::toShortDTO);
    }

    /**
     * Получает черновики пользователя.
     * Черновики доступны только их автору.
     *
     * @param user пользователь
     * @param pageable параметры пагинации
     * @return страница черновиков
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Transactional
    public Page<PostShortDTO> getUserDrafts(UserEntity user, Pageable pageable) {
        validateUserExists(user.getId());
        return postRepository.getDraftPosts(user.getId(), pageable)
                .map(PostMapper::toShortDTO);
    }

    /**
     * Получает черновики пользователя по его ID.
     * Доступно только администраторам.
     *
     * @param userId ID пользователя
     * @param pageable параметры пагинации
     * @return страница черновиков
     * @throws EntityNotFoundException если пользователь не найден
     */
    public Page<PostShortDTO> getDraftsByUserId(Long userId, Pageable pageable) {
        validateUserExists(userId);
        return postRepository.getDraftsByUserId(userId, pageable)
                .map(PostMapper::toShortDTO);
    }

    /**
     * Получает удаленные посты пользователя.
     *
     * @param authorId ID автора
     * @param pageable параметры пагинации
     * @return страница удаленных постов
     * @throws EntityNotFoundException если пользователь не найден
     */
    @Transactional
    public Page<PostShortDTO> getDeletedPostsByUser(Long authorId, Pageable pageable) {
        validateUserExists(authorId);
        return postRepository.getDeletedPostsByUser(authorId, pageable)
                .map(PostMapper::toShortDTO);
    }

    /**
     * Преобразует список DTO блоков контента в список сущностей блоков.
     *
     * @param blocks список DTO блоков
     * @return список сущностей блоков
     */
    private List<PostEntity.ContentBlock> mapContentBlocks(List<PostCreateDTO.ContentBlockDTO> blocks) {
        return blocks.stream()
                .map(this::mapContentBlock)
                .collect(Collectors.toList());
    }

    /**
     * Преобразует DTO блока контента в сущность блока.
     *
     * @param block DTO блока
     * @return сущность блока
     */
    private PostEntity.ContentBlock mapContentBlock(PostCreateDTO.ContentBlockDTO block) {
        return PostEntity.ContentBlock.builder()
                .type(block.type())
                .cover(block.cover())
                .hidden(block.hidden())
                .anchor(block.anchor())
                .data(block.data().metadata())
                .build();
    }

    /**
     * Преобразует строковое представление периода в LocalDateTime.
     *
     * @param period строковое представление периода
     * @return время начала периода или null
     * @throws IllegalArgumentException если период недопустим
     */
    private LocalDateTime parsePeriod(String period) {
        if (period == null) return null;
        
        return switch (period) {
            case "hour" -> LocalDateTime.now().minusHours(1);
            case "day" -> LocalDateTime.now().minusDays(1);
            case "week" -> LocalDateTime.now().minusWeeks(1);
            case "month" -> LocalDateTime.now().minusMonths(1);
            case "year" -> LocalDateTime.now().minusYears(1);
            case "all" -> null;
            default -> throw new IllegalArgumentException("Invalid period: " + period);
        };
    }

    /**
     * Находит посты по заданным критериям.
     *
     * @param lastSeenId ID последнего просмотренного поста
     * @param topicId ID темы
     * @param periodStart время начала периода
     * @param sort тип сортировки
     * @param limit максимальное количество постов
     * @return срез постов
     */
    private Slice<PostEntity> findPostsByCriteria(
            Long lastSeenId,
            Long topicId,
            LocalDateTime periodStart,
            String sort,
            int limit) {
        
        if (topicId != null) {
            return findPostsByTopicAndSort(lastSeenId, topicId, periodStart, sort, limit);
        }
        return findPostsWithoutTopic(lastSeenId, periodStart, sort, limit);
    }

    /**
     * Находит посты по теме и типу сортировки.
     *
     * @param lastSeenId ID последнего просмотренного поста
     * @param topicId ID темы
     * @param periodStart время начала периода
     * @param sort тип сортировки
     * @param limit максимальное количество постов
     * @return срез постов
     */
    private Slice<PostEntity> findPostsByTopicAndSort(
            Long lastSeenId,
            Long topicId,
            LocalDateTime periodStart,
            String sort,
            int limit) {
        
        return switch (sort != null ? sort : "newest") {
            case "top" -> postRepository.findTopPostsByPeriodAndTopic(lastSeenId, periodStart, topicId, limit);
            case "hot" -> postRepository.findHotPostsByTopic(lastSeenId, topicId, limit);
            case "best" -> postRepository.findBestPostsByTopic(lastSeenId, topicId, limit);
            default -> postRepository.findNewestPostsByTopic(lastSeenId, topicId, limit);
        };
    }

    /**
     * Находит посты без привязки к теме.
     *
     * @param lastSeenId ID последнего просмотренного поста
     * @param periodStart время начала периода
     * @param sort тип сортировки
     * @param limit максимальное количество постов
     * @return срез постов
     */
    private Slice<PostEntity> findPostsWithoutTopic(
            Long lastSeenId,
            LocalDateTime periodStart,
            String sort,
            int limit) {
        
        return switch (sort != null ? sort : "newest") {
            case "top" -> postRepository.findTopPostsByPeriod(lastSeenId, periodStart, limit);
            case "hot" -> postRepository.findHotPosts(lastSeenId, limit);
            case "best" -> postRepository.findBestPosts(lastSeenId, limit);
            default -> postRepository.findNewestPosts(lastSeenId, limit);
        };
    }

    /**
     * Находит пост по ID или выбрасывает исключение.
     *
     * @param id ID поста
     * @return найденный пост
     * @throws EntityNotFoundException если пост не найден
     */
    private PostEntity findPostOrThrow(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост не найден"));
    }

    /**
     * Находит тему по ID или выбрасывает исключение.
     *
     * @param id ID темы
     * @return найденная тема
     * @throws EntityNotFoundException если тема не найдена
     */
    private TopicEntity findTopicOrThrow(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Тема не найдена"));
    }

    /**
     * Проверяет существование пользователя.
     *
     * @param userId ID пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
    }
}