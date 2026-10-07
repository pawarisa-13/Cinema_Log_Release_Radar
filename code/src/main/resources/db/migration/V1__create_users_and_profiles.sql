-- users 1 ── 1 user_profiles  (One-to-One: login data is kept apart from public profile data)

CREATE TABLE users (
    id            BIGSERIAL    PRIMARY KEY,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE user_profiles (
    id           BIGSERIAL    PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    display_name VARCHAR(60)  NOT NULL,
    bio          VARCHAR(200),
    avatar_style VARCHAR(20)  NOT NULL DEFAULT 'BUN',
    avatar_color VARCHAR(20)  NOT NULL DEFAULT 'PINK',
    updated_at   TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT uq_user_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
