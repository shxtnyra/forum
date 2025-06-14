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

-- 2. Добавляем 1000 тестовых пользователей
DO $$
DECLARE
    first_names TEXT[] := ARRAY['Иван', 'Алексей', 'Дмитрий', 'Сергей', 'Андрей', 'Михаил', 'Артем', 'Павел', 'Николай', 'Егор'];
    last_names TEXT[] := ARRAY['Иванов', 'Петров', 'Сидоров', 'Смирнов', 'Кузнецов', 'Попов', 'Васильев', 'Федоров', 'Морозов', 'Новиков'];
    domains TEXT[] := ARRAY['gmail.com', 'yandex.ru', 'mail.ru', 'outlook.com', 'protonmail.com'];
    avatars TEXT[] := ARRAY[
        'https://i.pravatar.cc/150?img=1',
        'https://i.pravatar.cc/150?img=2',
        'https://i.pravatar.cc/150?img=3',
        'https://i.pravatar.cc/150?img=4',
        'https://i.pravatar.cc/150?img=5',
        'https://i.pravatar.cc/150?img=6',
        'https://i.pravatar.cc/150?img=7',
        'https://i.pravatar.cc/150?img=8',
        NULL
    ];
    nicknames TEXT[] := ARRAY['coder', 'hacker', 'dev', 'ninja', 'pro', 'master', 'newbie', 'geek', 'nerd', 'wizard', NULL];
    roles TEXT[] := ARRAY['Опытный разработчик', 'Начинающий программист', 'Студент', 'Преподаватель', 'Team Lead', 'CTO', 'Фрилансер', 'Участник форума'];
    tech_skills TEXT[] := ARRAY['Java', 'Python', 'JavaScript', 'C#', 'PHP', 'Go', 'Rust', 'SQL', 'NoSQL', 'React', 'Vue', 'Angular'];
BEGIN
    FOR i IN 1..1000 LOOP
        DECLARE
            first_name TEXT := first_names[1 + (i % array_length(first_names, 1))];
            last_name TEXT := last_names[1 + ((i+5) % array_length(last_names, 1))];
            v_username TEXT := 'user_' || i;
            v_email TEXT := LOWER(first_name) || '.' || LOWER(last_name) || i || '@' || domains[1 + (i % array_length(domains, 1))];
            v_nickname TEXT := CASE WHEN random() > 0.3 THEN nicknames[1 + (i % array_length(nicknames, 1))] || (i % 100) ELSE NULL END;
            v_avatar_url TEXT := avatars[1 + (i % array_length(avatars, 1))];
            v_description TEXT := roles[1 + (i % array_length(roles, 1))] || ' ' || tech_skills[1 + (i % array_length(tech_skills, 1))];
        BEGIN
            INSERT INTO users (
                username,
                email,
                password,
                is_confirmed,
                profile_description,
                registration_date,
                nickname,
                avatar_url
            ) VALUES (
                v_username,
                v_email,
                '$2a$10$Nsi6XvXvQNjEMZBMjtsmSu34XUvBv3SpTV69H041PPTyTL47FT9Ni', -- password: user123
                random() > 0.1, -- 90% подтвержденных
                v_description,
                NOW() - (i * INTERVAL '1 day') - (random() * INTERVAL '365 days'),
                v_nickname,
                v_avatar_url
            ) ON CONFLICT (username) DO NOTHING;
        END;
    END LOOP;
END $$;

-- 3. Добавляем 500,000 постов
DO $$
DECLARE
    topic_ids BIGINT[];
    user_ids BIGINT[];
    titles TEXT[] := ARRAY[
        'Как я решил проблему с', 
        'Вопрос по', 
        'Обсуждение', 
        'Новости о', 
        'Сравнение', 
        'Лучшие практики', 
        'Руководство по', 
        'Советы по', 
        'Изучаем', 
        'Проблемы с'
    ];
    title_suffixes TEXT[] := ARRAY[
        'для начинающих',
        'в 2023 году',
        'на практике',
        'с примерами',
        'в крупных проектах',
        'и альтернативы',
        'в продакшене',
        'в современных приложениях',
        'с нуля',
        'и как их избежать'
    ];
    content_patterns TEXT[] := ARRAY[
        'Приветствую! Столкнулся с проблемой %s. %s. Кто-нибудь знает как решить?',
        'Хочу поделиться своим опытом работы с %s. %s. Что думаете?',
        'Недавно изучил %s и хочу рассказать о своих находках. %s',
        'Как вы решаете проблему %s? %s. Интересно узнать разные подходы.',
        'Обзор технологии %s. %s. Готов ответить на вопросы.'
    ];
BEGIN
    -- Получаем ID всех тем и пользователей
    SELECT array_agg(id) INTO topic_ids FROM topics;
    SELECT array_agg(id) INTO user_ids FROM users WHERE username LIKE 'user_%';
    
    -- Генерируем 500,000 постов
    FOR i IN 1..500000 LOOP
        DECLARE
            v_topic_id BIGINT := topic_ids[1 + (i % array_length(topic_ids, 1))];
            v_user_id BIGINT := user_ids[1 + ((i*7) % array_length(user_ids, 1))];
            v_topic_name TEXT := (SELECT name FROM topics WHERE id = v_topic_id);
            v_title_pattern TEXT := titles[1 + (i % array_length(titles, 1))];
            v_title TEXT := v_title_pattern || ' ' || v_topic_name || ' ' || title_suffixes[1 + ((i+3) % array_length(title_suffixes, 1))];
            v_content_pattern TEXT := content_patterns[1 + (i % array_length(content_patterns, 1))];
            v_content TEXT := '';
            v_view_count INT := (random() * 1000)::INT;
            v_created_at TIMESTAMP := NOW() - (random() * INTERVAL '2 years');
        BEGIN
            -- Генерируем более осмысленное содержание
            CASE 
                WHEN i % 5 = 0 THEN 
                    v_content := format(v_content_pattern, v_topic_name, 
                        'Пробовал разные подходы, но ни один не дал удовлетворительного результата. Особенно сложно было разобраться с настройкой окружения.');
                WHEN i % 3 = 0 THEN 
                    v_content := format(v_content_pattern, v_topic_name, 
                        'Нашел интересное решение, которое значительно ускоряет процесс разработки. Хочу поделиться с сообществом.');
                ELSE 
                    v_content := format(v_content_pattern, v_topic_name, 
                        'Ищу советы от более опытных разработчиков. Какие лучшие практики вы можете порекомендовать?');
            END CASE;
            
            INSERT INTO posts (
                title,
                content,
                author_id,
                topic_id,
                created_at,
                view_count
            ) VALUES (
                v_title,
                v_content,
                v_user_id,
                v_topic_id,
                v_created_at,
                v_view_count
            );
            
            -- Выводим прогресс каждые 50,000 записей
            IF i % 50000 = 0 THEN
                RAISE NOTICE 'Добавлено % постов', i;
            END IF;
        END;
    END LOOP;
END $$;

-- 4. Добавляем 2,000,000 комментариев
DO $$
DECLARE
    post_ids BIGINT[];
    user_ids BIGINT[];
    comment_patterns TEXT[] := ARRAY[
        'Спасибо за полезный пост! Особенно помог раздел про %s.',
        'А есть ли аналогичное решение для %s?',
        'Попробуйте использовать %s, это должно решить вашу проблему.',
        'У меня была похожая проблема, решил через %s.',
        'Не согласен с автором в части про %s. На практике работает иначе.',
        'Отличный материал! Хотелось бы больше примеров по %s.',
        'А как насчет производительности при использовании %s?',
        'В документации к %s указано несколько иное поведение.',
        'Можно подробнее про %s? Не совсем понял этот момент.',
        'Проверьте настройки %s, возможно проблема там.'
    ];
BEGIN
    -- Получаем ID постов и пользователей
    SELECT array_agg(id) INTO post_ids FROM posts ORDER BY random() LIMIT 400000;
    SELECT array_agg(id) INTO user_ids FROM users WHERE username LIKE 'user_%';
    
    -- Генерируем 2,000,000 комментариев
    FOR i IN 1..2000000 LOOP
        DECLARE
            v_post_id BIGINT := post_ids[1 + (i % array_length(post_ids, 1))];
            v_user_id BIGINT := user_ids[1 + ((i*11) % array_length(user_ids, 1))];
            v_topic_name TEXT := (SELECT t.name FROM topics t JOIN posts p ON t.id = p.topic_id WHERE p.id = v_post_id);
            v_comment_pattern TEXT := comment_patterns[1 + (i % array_length(comment_patterns, 1))];
            v_text TEXT := '';
            v_level INT := 0;
            v_parent_id BIGINT := NULL;
            v_created_at TIMESTAMP := NOW() - (random() * INTERVAL '2 years');
        BEGIN
            -- Генерируем текст комментария
            v_text := format(v_comment_pattern, 
                CASE 
                    WHEN i % 7 = 0 THEN 'настройку окружения'
                    WHEN i % 5 = 0 THEN 'обработку ошибок'
                    WHEN i % 3 = 0 THEN 'оптимизацию производительности'
                    ELSE v_topic_name
                END);
            
            -- 70% корневых комментариев, 30% ответов
            IF random() > 0.7 THEN
                -- Выбираем случайный родительский комментарий для этого поста
                SELECT id INTO v_parent_id FROM comments 
                WHERE post_id = v_post_id AND parent_id IS NULL 
                ORDER BY random() 
                LIMIT 1;
                
                IF v_parent_id IS NOT NULL THEN
                    v_level := 1;
                    -- Делаем текст более похожим на ответ
                    v_text := 'В ответ на предыдущий комментарий: ' || v_text;
                END IF;
            END IF;
            
            INSERT INTO comments (
                text,
                author_id,
                post_id,
                parent_id,
                level,
                created_at
            ) VALUES (
                v_text,
                v_user_id,
                v_post_id,
                v_parent_id,
                v_level,
                v_created_at
            );
            
            -- Выводим прогресс каждые 200,000 записей
            IF i % 200000 = 0 THEN
                RAISE NOTICE 'Добавлено % комментариев', i;
            END IF;
        END;
    END LOOP;
END $$;

-- 5. Добавляем 5,000,000 оценок
DO $$
DECLARE
    post_ids BIGINT[];
    comment_ids BIGINT[];
    user_ids BIGINT[];
BEGIN
    -- Получаем ID постов, комментариев и пользователей
    SELECT array_agg(id) INTO post_ids FROM posts ORDER BY random() LIMIT 400000;
    SELECT array_agg(id) INTO comment_ids FROM comments ORDER BY random() LIMIT 1500000;
    SELECT array_agg(id) INTO user_ids FROM users WHERE username LIKE 'user_%';
    
    -- Генерируем 3,500,000 оценок постов
    FOR i IN 1..3500000 LOOP
        DECLARE
            v_post_id BIGINT := post_ids[1 + (i % array_length(post_ids, 1))];
            v_user_id BIGINT := user_ids[1 + ((i*13) % array_length(user_ids, 1))];
            v_is_like BOOLEAN := random() > 0.3; -- 70% лайков
            v_created_at TIMESTAMP := NOW() - (random() * INTERVAL '2 years');
        BEGIN
            INSERT INTO post_ratings (
                post_id,
                user_id,
                is_like,
                created_at
            ) VALUES (
                v_post_id,
                v_user_id,
                v_is_like,
                v_created_at
            ) ON CONFLICT DO NOTHING;
            
            -- Выводим прогресс каждые 500,000 записей
            IF i % 500000 = 0 THEN
                RAISE NOTICE 'Добавлено % оценок постов', i;
            END IF;
        END;
    END LOOP;
    
    -- Генерируем 1,500,000 оценок комментариев
    FOR i IN 1..1500000 LOOP
        DECLARE
            v_comment_id BIGINT := comment_ids[1 + (i % array_length(comment_ids, 1))];
            v_user_id BIGINT := user_ids[1 + ((i*17) % array_length(user_ids, 1))];
            v_is_like BOOLEAN := random() > 0.4; -- 60% лайков
            v_created_at TIMESTAMP := NOW() - (random() * INTERVAL '2 years');
        BEGIN
            INSERT INTO comment_ratings (
                comment_id,
                user_id,
                is_like,
                created_at
            ) VALUES (
                v_comment_id,
                v_user_id,
                v_is_like,
                v_created_at
            ) ON CONFLICT DO NOTHING;
            
            -- Выводим прогресс каждые 300,000 записей
            IF i % 300000 = 0 THEN
                RAISE NOTICE 'Добавлено % оценок комментариев', i;
            END IF;
        END;
    END LOOP;
END $$;

-- 6. Обновляем счетчики
-- Для постов
UPDATE posts p SET
    like_count = (SELECT COUNT(*) FROM post_ratings WHERE post_id = p.id AND is_like = true),
    dislike_count = (SELECT COUNT(*) FROM post_ratings WHERE post_id = p.id AND is_like = false)
WHERE EXISTS (SELECT 1 FROM post_ratings pr WHERE pr.post_id = p.id);

-- Для комментариев
UPDATE comments c SET
    like_count = (SELECT COUNT(*) FROM comment_ratings WHERE comment_id = c.id AND is_like = true),
    dislike_count = (SELECT COUNT(*) FROM comment_ratings WHERE comment_id = c.id AND is_like = false)
WHERE EXISTS (SELECT 1 FROM comment_ratings cr WHERE cr.comment_id = c.id);

-- 7. Добавляем дополнительные данные для пользователей
--UPDATE users u SET
--    total_rating = COALESCE((
--        SELECT SUM(CASE WHEN pr.is_like THEN 1 ELSE -1 END)
--        FROM posts p
--        JOIN post_ratings pr ON p.id = pr.post_id
--        WHERE p.author_id = u.id
--    ), 0) + COALESCE((
--        SELECT SUM(CASE WHEN cr.is_like THEN 1 ELSE -1 END) * 0.2
--        FROM comments c
--        JOIN comment_ratings cr ON c.id = cr.comment_id
--        WHERE c.author_id = u.id
--    ), 0),
--    post_count = (SELECT COUNT(*) FROM posts WHERE author_id = u.id),
--    comment_count = (SELECT COUNT(*) FROM comments WHERE author_id = u.id);