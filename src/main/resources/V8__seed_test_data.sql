-- 1. Добавляем 10 тематических разделов
INSERT INTO topics (name, slug, description) VALUES
    ('Java', 'java', 'Обсуждение Java и Spring Framework'),
    ('Python', 'python', 'Программирование на Python'),
    ('Web Development', 'web-dev', 'Frontend и Backend разработка'),
    ('Базы данных', 'databases', 'SQL, NoSQL, оптимизация запросов'),
    ('Мобильная разработка', 'mobile', 'Android, iOS, Flutter'),
    ('Искусственный интеллект', 'ai', 'Машинное обучение и нейросети'),
    ('DevOps', 'devops', 'Docker, Kubernetes, CI/CD'),
    ('Игровая разработка', 'gamedev', 'Unity, Unreal Engine'),
    ('Карьера в IT', 'career', 'Трудоустройство и развитие'),
    ('Алгоритмы', 'algorithms', 'Структуры данных и алгоритмы')
ON CONFLICT (slug) DO NOTHING;

-- 2. Добавляем 20 тестовых пользователей
DO $$
BEGIN
    FOR i IN 1..20 LOOP
        INSERT INTO users (
            username,
            email,
            password,
            is_confirmed,
            profile_description,
            registration_date
        ) VALUES (
            'user_' || i,
            'user_' || i || '@example.com',
            '$2a$10$N9qo8uLOickgx2ZMRZoMy.MQD/.G8L33E8LZnX3hEm6sZHCc8ag8u', -- password: user123
            TRUE,
            CASE
                WHEN i % 5 = 0 THEN 'Опытный разработчик'
                WHEN i % 3 = 0 THEN 'Начинающий программист'
                ELSE 'Участник форума'
            END,
            NOW() - (i * INTERVAL '2 days')
        ) ON CONFLICT (username) DO NOTHING;
    END LOOP;
END $$;

-- 3. Добавляем 50 постов (по 5 в каждой теме)
DO $$
DECLARE
    topic RECORD;
    user_id BIGINT;
BEGIN
    FOR topic IN SELECT id FROM topics LOOP
        FOR i IN 1..5 LOOP
            -- Выбираем случайного пользователя (исключая админа)
            SELECT id INTO user_id FROM users
            WHERE username LIKE 'user_%'
            ORDER BY RANDOM()
            LIMIT 1;

            INSERT INTO posts (
                title,
                content,
                author_id,
                topic_id,
                created_at,
                view_count
            ) VALUES (
                CASE
                    WHEN i = 1 THEN 'Лучшие практики в ' || (SELECT name FROM topics WHERE id = topic.id)
                    WHEN i = 2 THEN 'Вопрос по ' || (SELECT name FROM topics WHERE id = topic.id)
                    ELSE 'Обсуждение: ' || (SELECT name FROM topics WHERE id = topic.id) || ' #' || i
                END,
                'Это тестовое содержание поста ' || i || ' в теме ' ||
                (SELECT name FROM topics WHERE id = topic.id) || '. ' ||
                'Здесь должен быть полезный контент, но пока это просто пример.',
                user_id,
                topic.id,
                NOW() - (i * INTERVAL '3 hours'),
                FLOOR(RANDOM() * 100) + 10
            );
        END LOOP;
    END LOOP;
END $$;

-- 4. Добавляем 200 комментариев (в среднем по 4 на пост)
DO $$
DECLARE
    post RECORD;
    user_id BIGINT;
    parent_id BIGINT;
BEGIN
    FOR post IN SELECT id, author_id FROM posts ORDER BY RANDOM() LIMIT 50 LOOP
        -- Корневые комментарии (уровень 0)
        FOR i IN 1..3 LOOP
            SELECT id INTO user_id FROM users
            WHERE username LIKE 'user_%' AND id != post.author_id
            ORDER BY RANDOM()
            LIMIT 1;

            INSERT INTO comments (
                text,
                author_id,
                post_id,
                level,
                created_at
            ) VALUES (
                CASE
                    WHEN i = 1 THEN 'Интересный пост, спасибо!'
                    WHEN i = 2 THEN 'Есть вопрос по этому материалу...'
                    ELSE 'Дополнительная информация по теме: ...'
                END,
                user_id,
                post.id,
                0,
                NOW() - (i * INTERVAL '30 minutes')
            ) RETURNING id INTO parent_id;

            -- Вложенные комментарии (уровень 1)
            FOR j IN 1..2 LOOP
                INSERT INTO comments (
                    text,
                    author_id,
                    post_id,
                    parent_id,
                    level,
                    created_at
                ) VALUES (
                    CASE
                        WHEN j = 1 THEN 'Я согласен с этим комментарием'
                        ELSE 'У меня другое мнение по этому поводу'
                    END,
                    post.author_id,
                    post.id,
                    parent_id,
                    1,
                    NOW() - (j * INTERVAL '15 minutes')
                );
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

-- 5. Добавляем 500 оценок (лайки/дизлайки)
DO $$
DECLARE
    post RECORD;
    comment RECORD;
    user_id BIGINT;
BEGIN
    -- Оценки постов
    FOR post IN SELECT id FROM posts ORDER BY RANDOM() LIMIT 30 LOOP
        FOR i IN 1..15 LOOP
            SELECT id INTO user_id FROM users
            WHERE username LIKE 'user_%'
            ORDER BY RANDOM()
            LIMIT 1;

            INSERT INTO post_ratings (
                post_id,
                user_id,
                is_like,
                created_at
            ) VALUES (
                post.id,
                user_id,
                RANDOM() > 0.3, -- 70% лайков, 30% дизлайков
                NOW() - (i * INTERVAL '10 minutes')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;

    -- Оценки комментариев
    FOR comment IN SELECT id FROM comments ORDER BY RANDOM() LIMIT 50 LOOP
        FOR i IN 1..5 LOOP
            SELECT id INTO user_id FROM users
            WHERE username LIKE 'user_%'
            ORDER BY RANDOM()
            LIMIT 1;

            INSERT INTO comment_ratings (
                comment_id,
                user_id,
                is_like,
                created_at
            ) VALUES (
                comment.id,
                user_id,
                RANDOM() > 0.4, -- 60% лайков, 40% дизлайков
                NOW() - (i * INTERVAL '5 minutes')
            ) ON CONFLICT DO NOTHING;
        END LOOP;
    END LOOP;
END $$;

-- Обновляем счетчики (альтернатива триггерам)
UPDATE posts p SET
    like_count = (SELECT COUNT(*) FROM post_ratings WHERE post_id = p.id AND is_like = true),
    dislike_count = (SELECT COUNT(*) FROM post_ratings WHERE post_id = p.id AND is_like = false);

UPDATE comments c SET
    like_count = (SELECT COUNT(*) FROM comment_ratings WHERE comment_id = c.id AND is_like = true),
    dislike_count = (SELECT COUNT(*) FROM comment_ratings WHERE comment_id = c.id AND is_like = false);