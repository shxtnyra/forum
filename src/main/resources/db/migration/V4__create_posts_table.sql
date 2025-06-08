-- Основная таблица постов
CREATE TABLE posts (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    topic_id BIGINT NOT NULL REFERENCES topics(id) ON DELETE RESTRICT,
    view_count INTEGER NOT NULL DEFAULT 0,
    like_count INTEGER NOT NULL DEFAULT 0,
    dislike_count INTEGER NOT NULL DEFAULT 0,

    -- Ограничения
    CONSTRAINT chk_posts_view_count CHECK (view_count >= 0),
    CONSTRAINT chk_posts_counts CHECK (like_count >= 0 AND dislike_count >= 0)
);

-- Таблица рейтингов постов
CREATE TABLE post_ratings (
    id BIGSERIAL PRIMARY KEY,
    post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    is_like BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Уникальная пара post_id + user_id
    CONSTRAINT uk_rating_unique UNIQUE (post_id, user_id)
);

-- Индексы для постов
CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_posts_topic ON posts(topic_id);
CREATE INDEX idx_posts_created ON posts(created_at);
CREATE INDEX idx_posts_rating ON posts((like_count - dislike_count));

-- Индексы для рейтингов
CREATE INDEX idx_ratings_post ON post_ratings(post_id);
CREATE INDEX idx_ratings_user ON post_ratings(user_id);
CREATE INDEX idx_ratings_created ON post_ratings(created_at);