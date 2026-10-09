-- users 1 ── N watched_movies / watchlist_items / liked_movies / collections
-- collections 1 ── N collection_movies N ── 1 movies  (Many-to-Many with extra column added_at)

CREATE TABLE watched_movies (
    id           BIGSERIAL     PRIMARY KEY,
    user_id      BIGINT        NOT NULL,
    movie_id     BIGINT        NOT NULL,
    watched_date DATE          NOT NULL,
    rating       INTEGER,
    review       VARCHAR(1000),
    place        VARCHAR(10)   NOT NULL DEFAULT 'HOME',
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT now(),
    CONSTRAINT ck_watched_rating CHECK (rating IS NULL OR rating BETWEEN 1 AND 5),
    CONSTRAINT ck_watched_place  CHECK (place IN ('CINEMA', 'HOME', 'OTHER')),
    CONSTRAINT fk_watched_user  FOREIGN KEY (user_id)  REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_watched_movie FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE
);
CREATE INDEX idx_watched_user_date ON watched_movies (user_id, watched_date DESC);
CREATE INDEX idx_watched_movie     ON watched_movies (movie_id);

CREATE TABLE watchlist_items (
    id       BIGSERIAL   PRIMARY KEY,
    user_id  BIGINT      NOT NULL,
    movie_id BIGINT      NOT NULL,
    added_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_watchlist_user_movie UNIQUE (user_id, movie_id),
    CONSTRAINT fk_watchlist_user  FOREIGN KEY (user_id)  REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_watchlist_movie FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE
);

CREATE TABLE liked_movies (
    id       BIGSERIAL   PRIMARY KEY,
    user_id  BIGINT      NOT NULL,
    movie_id BIGINT      NOT NULL,
    liked_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_liked_user_movie UNIQUE (user_id, movie_id),
    CONSTRAINT fk_liked_user  FOREIGN KEY (user_id)  REFERENCES users (id)  ON DELETE CASCADE,
    CONSTRAINT fk_liked_movie FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE
);

CREATE TABLE collections (
    id          BIGSERIAL    PRIMARY KEY,
    user_id     BIGINT       NOT NULL,
    name        VARCHAR(60)  NOT NULL,
    description VARCHAR(200),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_collections_user_name UNIQUE (user_id, name),
    CONSTRAINT fk_collections_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE collection_movies (
    id            BIGSERIAL   PRIMARY KEY,
    collection_id BIGINT      NOT NULL,
    movie_id      BIGINT      NOT NULL,
    added_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_collection_movie UNIQUE (collection_id, movie_id),
    CONSTRAINT fk_cm_collection FOREIGN KEY (collection_id) REFERENCES collections (id) ON DELETE CASCADE,
    CONSTRAINT fk_cm_movie      FOREIGN KEY (movie_id)      REFERENCES movies (id)      ON DELETE CASCADE
);
CREATE INDEX idx_collection_movies_movie ON collection_movies (movie_id);
