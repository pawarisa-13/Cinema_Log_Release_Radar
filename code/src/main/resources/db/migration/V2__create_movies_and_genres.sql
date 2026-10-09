-- movies N ── M genres (Many-to-Many through movie_genres)
-- Movies are a local cache of TMDB data; tmdb_id is the natural key used for upserts.
CREATE TABLE genres (
    id INTEGER PRIMARY KEY,
    -- same id TMDB uses (27 = Horror, 16 = Animation …)
    name VARCHAR(50) NOT NULL,
    CONSTRAINT uq_genres_name UNIQUE (name)
);

CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    tmdb_id BIGINT NOT NULL,
    title VARCHAR(300) NOT NULL,
    original_title VARCHAR(300),
    overview TEXT,
    poster_path VARCHAR(200),
    backdrop_path VARCHAR(200),
    release_date DATE,
    runtime INTEGER,
    vote_average DOUBLE PRECISION NOT NULL DEFAULT 0,
    popularity DOUBLE PRECISION NOT NULL DEFAULT 0,
    original_language VARCHAR(10),
    director VARCHAR(200),
    cast_names VARCHAR(500),
    details_fetched_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_movies_tmdb UNIQUE (tmdb_id)
);

CREATE INDEX idx_movies_release_date ON movies (release_date);

CREATE INDEX idx_movies_popularity ON movies (popularity DESC);

CREATE INDEX idx_movies_vote_average ON movies (vote_average DESC);

CREATE INDEX idx_movies_title_lower ON movies (lower(title));

CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL,
    genre_id INTEGER NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_movie_genres_movie FOREIGN KEY (movie_id) REFERENCES movies (id) ON DELETE CASCADE,
    CONSTRAINT fk_movie_genres_genre FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);

CREATE INDEX idx_movie_genres_genre ON movie_genres (genre_id);