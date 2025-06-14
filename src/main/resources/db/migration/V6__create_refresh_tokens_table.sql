-- Таблица для хранения refresh-токенов
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    expiry_date TIMESTAMPTZ NOT NULL,

    -- Ограничения
    CONSTRAINT uk_refresh_token UNIQUE (token),
    CONSTRAINT chk_token_expiry CHECK (expiry_date > NOW())
);

-- Индексы
CREATE INDEX idx_refresh_tokens_user ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_expiry ON refresh_tokens(expiry_date);

-- Комментарии
COMMENT ON TABLE refresh_tokens IS 'Хранилище refresh-токенов для аутентификации';
COMMENT ON COLUMN refresh_tokens.expiry_date IS 'Время истечения токена (TIMESTAMPTZ с часовым поясом)';