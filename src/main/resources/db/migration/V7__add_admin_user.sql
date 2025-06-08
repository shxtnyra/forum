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
    '$2a$10$x5D2rV5z3J5XyYQlT7sZ.e5vUOv5U1wYb6X3dKjvQwLmN1Yb6X3dKj',
    'ROLE_ADMIN',
    TRUE,
    'System Administrator',
    NOW()
) ON CONFLICT (username) DO NOTHING;