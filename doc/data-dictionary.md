# Data dictionary

PostgreSQL 16. Schema created by Flyway (`code/src/main/resources/db/migration`). All timestamps are `TIMESTAMPTZ` (UTC).

## users — login account
| Column | Type | Null | Key / constraint | Description |
|---|---|---|---|---|
| id | BIGSERIAL | no | PK | |
| email | VARCHAR(255) | no | UNIQUE `uq_users_email` | lower-cased login email |
| password_hash | VARCHAR(100) | no | | BCrypt hash |
| created_at | TIMESTAMPTZ | no | default now() | |

## user_profiles — public profile (1:1 users)
| Column | Type | Null | Key / constraint | Description |
|---|---|---|---|---|
| id | BIGSERIAL | no | PK | |
| user_id | BIGINT | no | FK → users.id ON DELETE CASCADE, UNIQUE (makes it 1:1) | |
| display_name | VARCHAR(60) | no | | |
| bio | VARCHAR(200) | yes | | |
| avatar_style | VARCHAR(20) | no | default 'BUN' | BUN / BOB / CAP / CURLY |
| avatar_color | VARCHAR(20) | no | default 'PINK' | PINK / BLUE / GREEN / YELLOW / LAVENDER |
| updated_at | TIMESTAMPTZ | no | | |

## user_favorite_genres — profile N:M genres
| Column | Type | Key |
|---|---|---|
| profile_id | BIGINT | PK, FK → user_profiles.id ON DELETE CASCADE |
| genre_id | INTEGER | PK, FK → genres.id ON DELETE CASCADE |

## genres
| Column | Type | Null | Key | Description |
|---|---|---|---|---|
| id | INTEGER | no | PK | TMDB genre id (27 = Horror) |
| name | VARCHAR(50) | no | UNIQUE | |

## movies — local cache of TMDB
| Column | Type | Null | Key / index | Description |
|---|---|---|---|---|
| id | BIGSERIAL | no | PK | |
| tmdb_id | BIGINT | no | UNIQUE `uq_movies_tmdb` | used for upserts |
| title | VARCHAR(300) | no | index on lower(title) | |
| original_title | VARCHAR(300) | yes | | |
| overview | TEXT | yes | | |
| poster_path / backdrop_path | VARCHAR(200) | yes | | TMDB image paths |
| release_date | DATE | yes | index | drives "now showing" / "coming soon" |
| runtime | INTEGER | yes | | minutes, filled by the details call |
| vote_average | DOUBLE PRECISION | no | index (desc) | TMDB score 0–10 |
| popularity | DOUBLE PRECISION | no | index (desc) | default sort |
| original_language | VARCHAR(10) | yes | | ISO 639-1 |
| director | VARCHAR(200) | yes | | |
| cast_names | VARCHAR(500) | yes | | comma-separated top billed |
| details_fetched_at | TIMESTAMPTZ | yes | | details refreshed after 7 days |
| created_at | TIMESTAMPTZ | no | | "recently added" sort |

## movie_genres — movies N:M genres
| Column | Type | Key |
|---|---|---|
| movie_id | BIGINT | PK, FK → movies.id ON DELETE CASCADE |
| genre_id | INTEGER | PK, FK → genres.id ON DELETE CASCADE, index |

## watched_movies — diary entries (users 1:N, movies 1:N)
| Column | Type | Null | Key / constraint | Description |
|---|---|---|---|---|
| id | BIGSERIAL | no | PK | |
| user_id | BIGINT | no | FK → users ON DELETE CASCADE; index (user_id, watched_date desc) | |
| movie_id | BIGINT | no | FK → movies ON DELETE CASCADE; index | |
| watched_date | DATE | no | | |
| rating | INTEGER | yes | CHECK 1–5 | |
| review | VARCHAR(1000) | yes | | |
| place | VARCHAR(10) | no | CHECK CINEMA/HOME/OTHER | |
| created_at, updated_at | TIMESTAMPTZ | no | | |

## watchlist_items / liked_movies (users 1:N)
| Column | Type | Key / constraint |
|---|---|---|
| id | BIGSERIAL | PK |
| user_id | BIGINT | FK → users ON DELETE CASCADE |
| movie_id | BIGINT | FK → movies ON DELETE CASCADE |
| added_at / liked_at | TIMESTAMPTZ | |
| | | UNIQUE (user_id, movie_id) — a movie can be listed once |

## collections (users 1:N) and collection_movies (collections N:M movies)
| Table.Column | Type | Key / constraint |
|---|---|---|
| collections.id | BIGSERIAL | PK |
| collections.user_id | BIGINT | FK → users ON DELETE CASCADE |
| collections.name | VARCHAR(60) | UNIQUE (user_id, name) |
| collections.description | VARCHAR(200) | |
| collection_movies.id | BIGSERIAL | PK |
| collection_movies.collection_id | BIGINT | FK → collections ON DELETE CASCADE |
| collection_movies.movie_id | BIGINT | FK → movies ON DELETE CASCADE, index |
| collection_movies.added_at | TIMESTAMPTZ | UNIQUE (collection_id, movie_id) |

## reminders (users 1:N, movies 1:N)
| Column | Type | Constraint | Description |
|---|---|---|---|
| id | BIGSERIAL | PK | |
| user_id / movie_id | BIGINT | FK ON DELETE CASCADE, UNIQUE (user_id, movie_id) | one reminder per movie |
| offset_days | INTEGER | CHECK IN (0,1,3,7) | days before release |
| reminder_date | DATE | index (status, reminder_date) | release_date − offset |
| channel | VARCHAR(10) | CHECK IN_APP/EMAIL | Strategy key |
| status | VARCHAR(12) | CHECK SCHEDULED/SENT/CANCELLED | State pattern |
| created_at, sent_at | TIMESTAMPTZ | | |

## notifications (users 1:N)
| Column | Type | Constraint | Description |
|---|---|---|---|
| id | BIGSERIAL | PK | |
| user_id | BIGINT | FK → users ON DELETE CASCADE; index (user_id, created_at desc) | |
| movie_id | BIGINT | FK → movies ON DELETE SET NULL | optional |
| type | VARCHAR(20) | | RELEASE_REMINDER / NOW_IN_THEATERS / REVIEW_ADDED |
| channel | VARCHAR(10) | | |
| message | VARCHAR(300) | | |
| is_read | BOOLEAN | default false | |
| created_at | TIMESTAMPTZ | | |

## Cascade and fetch decisions
- **ON DELETE CASCADE** from users to everything they own: deleting an account leaves no orphans.
- **ON DELETE SET NULL** on notifications.movie_id: a notification is still readable without its movie.
- JPA: `User.profile` EAGER + cascade ALL (always shown together); all `@ManyToOne` are LAZY with `@EntityGraph` on the queries that need the movie; `Movie.genres` LAZY + `@BatchSize(50)` to avoid N+1 on catalog pages; `MovieCollection.items` cascade ALL + orphanRemoval so removing an item deletes its row.
