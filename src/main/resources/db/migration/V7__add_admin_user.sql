-- Пароль: 'admin123' (хеш BCrypt)
INSERT INTO users (
    username,
    email,
    password,
    role,
    is_confirmed,
    profile_description,
    registration_date
) VALUES (
    'admin',
    'admin@example.com',
    '$2a$10$x6OArveh./3yAFbSxjoAQ.TikReDXpGjDU7UP9XvmqP0074WLLQb6',
    'ROLE_ADMIN',
    TRUE,
    'System Administrator',
    NOW()
) ON CONFLICT (username) DO NOTHING;