package com.shxtnyra.forum.repository;

import com.shxtnyra.forum.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Репозиторий для работы с постами.
 * Предоставляет методы для поиска, создания, обновления и удаления постов,
 * а также для работы с черновиками и статистикой.
 */
public interface PostRepository extends JpaRepository<PostEntity, Long> {
    /**
     * Находит топ постов за указанный период по всем темам.
     * Сортировка по количеству реакций (лайки + дизлайки).
     *
     * @param lastSeenId ID последнего просмотренного поста для пагинации
     * @param periodStart начало периода
     * @param limit максимальное количество постов
     * @return срез постов, отсортированных по популярности
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId)
            AND p.createdAt >= :periodStart
            AND p.invisible = false
            AND p.deleted = false
            AND p.draft = false
            ORDER BY (p.likeCount + p.dislikeCount) DESC, p.id DESC
            """)
    Slice<PostEntity> findTopPostsByPeriod(
            @Param("lastSeenId") Long lastSeenId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("limit") int limit
    );

    /**
     * Находит топ постов за указанный период для конкретной темы.
     * Сортировка по количеству реакций (лайки + дизлайки).
     *
     * @param lastSeenId ID последнего просмотренного поста для пагинации
     * @param periodStart начало периода
     * @param topicId ID темы
     * @param limit максимальное количество постов
     * @return срез постов, отсортированных по популярности
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId)
            AND p.topic.id = :topicId
            AND p.createdAt >= :periodStart
            AND p.invisible = false
            AND p.deleted = false
            AND p.draft = false
            ORDER BY (p.likeCount + p.dislikeCount) DESC, p.id DESC
            """)
    Slice<PostEntity> findTopPostsByPeriodAndTopic(
            @Param("lastSeenId") Long lastSeenId,
            @Param("periodStart") LocalDateTime periodStart,
            @Param("topicId") Long topicId,
            @Param("limit") int limit
    );

    /**
     * Находит самые свежие посты по всем темам.
     * Сортировка по ID (в обратном порядке).
     *
     * @param lastSeenId ID последнего просмотренного поста для пагинации
     * @param limit максимальное количество постов
     * @return срез постов, отсортированных по времени создания
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId)
            AND p.invisible = false
            AND p.deleted = false
            AND p.draft = false
            ORDER BY p.id DESC
            """)
    Slice<PostEntity> findNewestPosts(
            @Param("lastSeenId") Long lastSeenId,
            @Param("limit") int limit
    );

    /**
     * Находит самые свежие посты для конкретной темы.
     * Сортировка по ID (в обратном порядке).
     *
     * @param lastSeenId ID последнего просмотренного поста для пагинации
     * @param topicId ID темы
     * @param limit максимальное количество постов
     * @return срез постов, отсортированных по времени создания
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE (:lastSeenId IS NULL OR p.id < :lastSeenId)
            AND p.topic.id = :topicId
            AND p.invisible = false
            AND p.deleted = false
            AND p.draft = false
            ORDER BY p.id DESC
            """)
    Slice<PostEntity> findNewestPostsByTopic(
            @Param("lastSeenId") Long lastSeenId,
            @Param("topicId") Long topicId,
            @Param("limit") int limit
    );

    /**
     * Увеличивает счетчик просмотров поста на 1.
     *
     * @param id ID поста
     */
    @Modifying
    @Query("UPDATE PostEntity p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);

    /**
     * Находит ID автора поста по ID поста.
     *
     * @param id ID поста
     * @return Optional с ID автора
     */
    @Query("SELECT p.author.id FROM PostEntity p WHERE p.id = :id")
    Optional<Long> findAuthorIdById(@Param("id") Long id);

    /**
     * Увеличивает счетчик лайков поста на указанное значение.
     *
     * @param postId ID поста
     * @param increment значение для увеличения
     */
    @Modifying
    @Query("UPDATE PostEntity p SET p.likeCount = p.likeCount + :increment WHERE p.id = :postId")
    void incrementLikeCount(@Param("postId") Long postId, @Param("increment") int increment);

    /**
     * Увеличивает счетчик дизлайков поста на указанное значение.
     *
     * @param postId ID поста
     * @param increment значение для увеличения
     */
    @Modifying
    @Query("UPDATE PostEntity p SET p.dislikeCount = p.dislikeCount + :increment WHERE p.id = :postId")
    void incrementDislikeCount(@Param("postId") Long postId, @Param("increment") int increment);

    /**
     * Обновляет счетчики лайков и дизлайков поста.
     *
     * @param postId ID поста
     * @param likeDelta изменение количества лайков
     * @param dislikeDelta изменение количества дизлайков
     */
    @Modifying
    @Query("""
            UPDATE PostEntity p SET
            p.likeCount = p.likeCount + :likeDelta,
            p.dislikeCount = p.dislikeCount + :dislikeDelta
            WHERE p.id = :postId
            """)
    void updateRatingCounters(@Param("postId") Long postId,
                              @Param("likeDelta") int likeDelta,
                              @Param("dislikeDelta") int dislikeDelta);

    /**
     * Находит невидимые посты с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница невидимых постов
     */
    @Query("SELECT p FROM PostEntity p WHERE p.invisible = true AND p.deleted = false")
    Page<PostEntity> getInvisiblePosts(Pageable pageable);

    /**
     * Находит посты пользователя по видимости.
     *
     * @param userId ID пользователя
     * @param isInvisible флаг видимости
     * @param pageable параметры пагинации
     * @return страница постов пользователя
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE p.author.id = :userId
            AND p.invisible = :isInvisible
            AND p.deleted = false
            AND p.draft = false
            """)
    Page<PostEntity> getPostsByUserByVisibility(Long userId, boolean isInvisible, Pageable pageable);

    /**
     * Находит удаленные посты пользователя.
     *
     * @param userId ID пользователя
     * @param pageable параметры пагинации
     * @return страница удаленных постов
     */
    @Query("SELECT p FROM PostEntity p WHERE p.author.id = :userId AND p.deleted = true")
    Page<PostEntity> getDeletedPostsByUser(@Param("userId") Long userId, Pageable pageable);

    /**
     * Находит черновики пользователя.
     * Возвращает только неудаленные черновики с пагинацией.
     *
     * @param authorId ID автора
     * @param pageable параметры пагинации
     * @return страница черновиков пользователя
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE p.author.id = :authorId
            AND p.draft = true
            AND p.deleted = false
            """)
    Page<PostEntity> getDraftPosts(@Param("authorId") Long authorId, Pageable pageable);

    /**
     * Ищет посты по заголовку с возможностью фильтрации по теме.
     *
     * @param query поисковый запрос
     * @param topicId ID темы (опционально)
     * @param pageable параметры пагинации
     * @return страница найденных постов
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%'))
            AND (:topicId IS NULL OR p.topic.id = :topicId)
            AND p.invisible = false
            AND p.deleted = false
            AND p.draft = false
            """)
    Page<PostEntity> searchPosts(
            @Param("query") String query,
            @Param("topicId") Long topicId,
            Pageable pageable
    );

    /**
     * Находит пост по ID с учетом видимости и прав доступа.
     *
     * @param id ID поста
     * @param currentUserId ID текущего пользователя
     * @return Optional с найденным постом
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE p.id = :id
            AND p.invisible = false
            AND p.deleted = false
            AND (p.draft = false OR p.author.id = :currentUserId)
            """)
    Optional<PostEntity> findByIdAndVisibility(
            @Param("id") Long id,
            @Param("currentUserId") Long currentUserId
    );

    /**
     * Находит пост по customUri с учетом видимости и прав доступа.
     *
     * @param customUri пользовательский URI поста
     * @param currentUserId ID текущего пользователя
     * @return Optional с найденным постом
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE p.customUri = :customUri
            AND p.invisible = false
            AND p.deleted = false
            AND (p.draft = false OR p.author.id = :currentUserId)
            """)
    Optional<PostEntity> findByCustomUriAndVisibility(
            @Param("customUri") String customUri,
            @Param("currentUserId") Long currentUserId
    );

    /**
     * Проверяет существование поста с указанным customUri.
     *
     * @param customUri пользовательский URI поста
     * @return true если пост существует
     */
    boolean existsByCustomUri(String customUri);

    /**
     * Находит черновик поста по ID и автору.
     *
     * @param postId ID поста
     * @param authorId ID автора
     * @return Optional с найденным черновиком
     */
    @Query("""
            SELECT p FROM PostEntity p
            WHERE p.author.id = :authorId
            AND p.id = :postId
            AND p.draft = true
            AND p.deleted = false
            """)
    Optional<PostEntity> findDraftByIdAndAuthor(
            @Param("postId") Long postId,
            @Param("authorId") Long authorId
    );
}
