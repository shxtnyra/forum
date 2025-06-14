-- Таблица жалоб
CREATE TABLE reports (
    id BIGSERIAL PRIMARY KEY,
    author_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    post_id BIGINT REFERENCES posts(id) ON DELETE CASCADE,
    comment_id BIGINT REFERENCES comments(id) ON DELETE CASCADE,
    solved BOOLEAN NOT NULL DEFAULT FALSE,
    reason VARCHAR(20) NOT NULL,
    reported_at TIMESTAMP NOT NULL DEFAULT NOW(),

    CHECK (
        (post_id IS NOT NULL AND comment_id IS NULL) OR
        (post_id IS NULL AND comment_id IS NOT NULL)
    )
);

-- Индексы для жалоб
CREATE INDEX idx_reports_author ON reports(author_id);
CREATE INDEX idx_reports_comment ON reports(comment_id);
CREATE INDEX idx_reports_post ON reports(post_id);
CREATE INDEX idx_reports_solved ON reports(solved);