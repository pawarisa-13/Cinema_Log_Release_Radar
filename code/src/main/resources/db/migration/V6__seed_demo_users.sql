-- Demo accounts (password for all: cinema123). Use them for the presentation and Swagger testing.

INSERT INTO users (email, password_hash) VALUES
 ('demo@cinemalog.app',  '$2a$10$vcWLjQeT86kfhNDNkxoYCOBY1Wv2IEs40IEbx0vcDIC.ApoIKLhW6'),
 ('mochi@cinemalog.app', '$2a$10$vcWLjQeT86kfhNDNkxoYCOBY1Wv2IEs40IEbx0vcDIC.ApoIKLhW6'),
 ('nao@cinemalog.app',   '$2a$10$vcWLjQeT86kfhNDNkxoYCOBY1Wv2IEs40IEbx0vcDIC.ApoIKLhW6'),
 ('toast@cinemalog.app', '$2a$10$vcWLjQeT86kfhNDNkxoYCOBY1Wv2IEs40IEbx0vcDIC.ApoIKLhW6');

INSERT INTO user_profiles (user_id, display_name, bio, avatar_style, avatar_color)
SELECT u.id, v.display_name, v.bio, v.avatar_style, v.avatar_color
FROM (VALUES
 ('demo@cinemalog.app',  'Pawarisa',    'collecting little movie moments. horror on fridays, ghibli on sundays.', 'BUN',   'PINK'),
 ('mochi@cinemalog.app', 'mochi_reels', 'animation first, questions later.',                                       'BOB',   'BLUE'),
 ('nao@cinemalog.app',   'nao.watches', 'one horror film every night in october.',                                 'CURLY', 'GREEN'),
 ('toast@cinemalog.app', 'film_toast',  'cries at everything, rates everything.',                                  'CAP',   'YELLOW')
) AS v(email, display_name, bio, avatar_style, avatar_color)
JOIN users u ON u.email = v.email;

INSERT INTO user_favorite_genres (profile_id, genre_id)
SELECT p.id, v.genre_id
FROM (VALUES
 ('demo@cinemalog.app', 27), ('demo@cinemalog.app', 16), ('demo@cinemalog.app', 14),
 ('mochi@cinemalog.app', 16), ('nao@cinemalog.app', 27), ('toast@cinemalog.app', 18)
) AS v(email, genre_id)
JOIN users u ON u.email = v.email
JOIN user_profiles p ON p.user_id = u.id;
