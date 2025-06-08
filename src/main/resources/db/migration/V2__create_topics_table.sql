-- Таблица тематических разделов
CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(50) NOT NULL,
    preview_url TEXT,
    description VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    last_modified_at TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Ограничения уникальности
    CONSTRAINT uk_topics_slug UNIQUE (slug),

    -- Проверочные ограничения
    CONSTRAINT chk_topics_slug_format CHECK (slug ~ '^[a-z0-9-]+$')
);

-- Индексы
CREATE INDEX idx_topics_slug ON topics(slug);

-- Комментарии
COMMENT ON TABLE topics IS 'Тематические разделы форума';
COMMENT ON COLUMN topics.slug IS 'Человеко-читаемый URL-идентификатор (латиница, цифры, дефисы)';