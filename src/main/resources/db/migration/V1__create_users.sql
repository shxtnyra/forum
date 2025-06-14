-- Таблица пользователей
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    email VARCHAR(255) NOT NULL,
    is_confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(40),
    nickname VARCHAR(32),
    profile_description VARCHAR(150) NOT NULL,
    avatar_url VARCHAR(255),
    registration_date TIMESTAMP NOT NULL DEFAULT NOW(),
    total_rating DOUBLE PRECISION NOT NULL DEFAULT 0,
    weekly_rating DOUBLE PRECISION NOT NULL DEFAULT 0,
    last_activity TIMESTAMP,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',

    -- Ограничения уникальности
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT uk_users_nickname UNIQUE (nickname)
);

-- Индексы
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_nickname ON users(nickname);
CREATE INDEX idx_users_registration_date ON users(registration_date);

-- Комментарии к таблице и полям (опционально)
COMMENT ON TABLE users IS 'Таблица для хранения данных пользователей';
COMMENT ON COLUMN users.role IS 'Роль пользователя: ROLE_USER, ROLE_MODERATOR, ROLE_ADMIN';