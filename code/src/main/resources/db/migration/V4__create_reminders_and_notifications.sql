-- user_profiles N ── M genres (favorite genres)
-- users 1 ── N reminders, users 1 ── N notifications

CREATE TABLE user_favorite_genres (
    profile_id BIGINT  NOT NULL,
    genre_id   INTEGER NOT NULL,
    PRIMARY KEY (profile_id, genre_id),
    CONSTRAINT fk_fav_profile FOREIGN KEY (profile_id) REFERENCES user_profiles (id) ON DELETE CASCADE,
    CONSTRAINT fk_fav_genre   FOREIGN KEY (genre_id)   REFERENCES genres (id)        ON DELETE CASCADE
);

CREATE TABLE reminders (
    id            BIGSERIAL   PRIMARY KEY,
    user_id       BIGINT      NOT NULL,
    movie_id      BIGINT      NOT NULL,
    offset_days   INTEGER     NOT NULL,
    reminder_date DATE        NOT NULL,
    channel       VARCHAR(10) NOT NULL DEFAULT 'IN_APP',
    status        VARCHAR(12) NOT NULL DEFAULT 'SCHEDULED',
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    sent_at       TIMESTAMPTZ,
    CONSTRAINT uq_reminders_user_movie UNIQUE (user_id, movie_id),
    CONSTRAINT ck_reminders_offset  CHECK (offset_days IN (0, 1, 3, 7)),
    CONSTRAINT ck_reminders_channel CHECK (channel IN ('IN_APP', 'EMAIL')),
    CONSTRAINT ck_reminders_status  CHECK (status IN ('SCHEDULED', 'SENT', 'CANCELLED')),
    CONSTRAINT fk_reminders_user  FOREIGN KEY (user_id)  REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_reminders_movie FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE
);
-- the hourly job looks up "SCHEDULED and due" reminders
CREATE INDEX idx_reminders_due ON reminders (status, reminder_date);

CREATE TABLE notifications (
    id         BIGSERIAL    PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    movie_id   BIGINT,
    type       VARCHAR(20)  NOT NULL,
    channel    VARCHAR(10)  NOT NULL,
    message    VARCHAR(300) NOT NULL,
    is_read    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT fk_notifications_user  FOREIGN KEY (user_id)  REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_notifications_movie FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE SET NULL
);
CREATE INDEX idx_notifications_user_created ON notifications (user_id, created_at DESC);
