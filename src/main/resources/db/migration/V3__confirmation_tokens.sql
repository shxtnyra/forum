-- Таблица для токенов подтверждения
CREATE TABLE confirmation_tokens (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP NULL,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT confirmation_tokens_token_unique UNIQUE (token)
);

-- Индексы
CREATE INDEX confirmation_tokens_token_idx ON confirmation_tokens(token);
CREATE INDEX confirmation_tokens_user_id_idx ON confirmation_tokens(user_id);
CREATE INDEX confirmation_tokens_expires_at_idx ON confirmation_tokens(expires_at);