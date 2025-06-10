-- Таблица комментариев
CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    text TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    parent_id BIGINT REFERENCES comments(id) ON DELETE SET NULL,
    level INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0 CHECK (like_count >= 0),
    dislike_count INTEGER NOT NULL DEFAULT 0 CHECK (dislike_count >= 0),
    deleted BOOLEAN NOT NULL DEFAULT FALSE,

    -- Ограничения
    CONSTRAINT chk_comment_level CHECK (level BETWEEN 0 AND 10) -- Защита от бесконечной вложенности
);

-- Таблица оценок комментариев
CREATE TABLE comment_ratings (
    id BIGSERIAL PRIMARY KEY,
    comment_id BIGINT NOT NULL REFERENCES comments(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_like BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Один пользователь - одна оценка на комментарий
    CONSTRAINT uk_comment_rating_unique UNIQUE (comment_id, user_id)
);

-- Индексы для комментариев
CREATE INDEX idx_comments_post ON comments(post_id);
CREATE INDEX idx_comments_author ON comments(author_id);
CREATE INDEX idx_comments_parent ON comments(parent_id) WHERE parent_id IS NOT NULL;
CREATE INDEX idx_comments_created ON comments(created_at);

-- Индексы для оценок
CREATE INDEX idx_comment_ratings_comment ON comment_ratings(comment_id);
CREATE INDEX idx_comment_ratings_user ON comment_ratings(user_id);

-- Комментарии к таблицам
COMMENT ON TABLE comments IS 'Иерархические комментарии к постам';
COMMENT ON COLUMN comments.level IS 'Уровень вложенности (0 - корневой комментарий)';
COMMENT ON TABLE comment_ratings IS 'Оценки комментариев (лайки/дизлайки)';