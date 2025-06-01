package com.shxtnyra.forum.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сущность поста.
 * Представляет собой запись в блоге или на форуме, содержащую заголовок,
 * блоки контента и метаданные.
 */
@Entity
@Table(name = "posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity {

    /**
     * Уникальный идентификатор поста.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Заголовок поста.
     * Не может быть пустым.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Пользовательский URI поста.
     * Используется для создания SEO-friendly URL.
     * Должен быть уникальным.
     */
    @Column(unique = true)
    private String customUri;

    /**
     * Блоки контента поста.
     * Хранятся в формате JSONB в базе данных.
     * Каждый блок может содержать различный тип контента (текст, изображение, видео и т.д.).
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb", nullable = false)
    @Builder.Default
    private List<ContentBlock> contentBlocks = new ArrayList<>();

    /**
     * Дата и время создания поста.
     * Устанавливается автоматически при создании и не может быть изменена.
     */
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    /**
     * Дата и время последнего обновления поста.
     * Обновляется автоматически при каждом изменении.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Автор поста.
     * Связь многие-к-одному с сущностью пользователя.
     * Загружается лениво для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    /**
     * Тема поста.
     * Связь многие-к-одному с сущностью темы.
     * Загружается лениво для оптимизации производительности.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    private TopicEntity topic;

    /**
     * Количество просмотров поста.
     * По умолчанию равно 0.
     */
    @Column(nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private int viewCount = 0;

    /**
     * Количество лайков поста.
     * По умолчанию равно 0.
     */
    @Column(nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private int likeCount = 0;

    /**
     * Количество дизлайков поста.
     * По умолчанию равно 0.
     */
    @Column(nullable = false, columnDefinition = "integer default 0")
    @Builder.Default
    private int dislikeCount = 0;

    /**
     * Флаг видимости поста.
     * Если true, пост скрыт от обычных пользователей.
     * По умолчанию false.
     */
    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean invisible = false;

    /**
     * Флаг удаления поста.
     * Если true, пост считается удаленным (мягкое удаление).
     * По умолчанию false.
     */
    @Column(nullable = false, columnDefinition = "boolean default false")
    @Builder.Default
    private boolean deleted = false;

    /**
     * Флаг черновика.
     * Если true, пост является черновиком и не виден публично.
     * По умолчанию true.
     */
    @Column(nullable = false, columnDefinition = "boolean default true")
    @Builder.Default
    private boolean draft = true;

    /**
     * Встроенный класс для представления блока контента.
     * Каждый блок имеет тип, флаги и данные.
     */
    @Embeddable
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentBlock {
        /**
         * Тип блока контента.
         * Определяет, как будет отображаться и обрабатываться блок.
         */
        @Column(nullable = false)
        private String type;
        
        /**
         * Флаг обложки.
         * Если true, блок является обложкой поста.
         * По умолчанию false.
         */
        @Column(nullable = false, columnDefinition = "boolean default false")
        @Builder.Default
        private boolean cover = false;
        
        /**
         * Флаг скрытия.
         * Если true, блок скрыт от обычных пользователей.
         * По умолчанию false.
         */
        @Column(nullable = false, columnDefinition = "boolean default false")
        @Builder.Default
        private boolean hidden = false;
        
        /**
         * Якорь блока.
         * Используется для создания ссылок на конкретный блок в посте.
         */
        private String anchor;
        
        /**
         * Данные блока.
         * Хранятся в формате JSONB в базе данных.
         * Содержат специфичные для типа блока данные.
         */
        @JdbcTypeCode(SqlTypes.JSON)
        @Column(columnDefinition = "jsonb")
        private Map<String, Object> data;
    }

    /**
     * Метод, вызываемый перед сохранением новой сущности.
     * Устанавливает даты создания и обновления.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Метод, вызываемый перед обновлением сущности.
     * Обновляет дату последнего изменения.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}