-- Demo diary, watchlist, likes and collections. Dates are relative to the day the migration runs,
-- so the diary calendar always has entries in the current and previous month.

INSERT INTO watched_movies (user_id, movie_id, watched_date, rating, review, place)
SELECT u.id, m.id, CURRENT_DATE - v.days_ago, v.rating, v.review, v.place
FROM (VALUES
 ('demo@cinemalog.app', 14836,  31, 4, 'the button eyes still get me every single time.', 'HOME'),
 ('demo@cinemalog.app', 129,    28, 5, 'rewatch number six. still crying at the train scene.', 'HOME'),
 ('demo@cinemalog.app', 493922, 26, 4, NULL, 'HOME'),
 ('demo@cinemalog.app', 16859,  20, 5, 'a hug in movie form.', 'HOME'),
 ('demo@cinemalog.app', 138843, 15, 4, 'pretty scary but I loved the atmosphere.', 'CINEMA'),
 ('demo@cinemalog.app', 4977,   15, 4, 'double feature night!', 'CINEMA'),
 ('demo@cinemalog.app', 530385, 15, 3, NULL, 'HOME'),
 ('demo@cinemalog.app', 12429,  12, 4, 'HAM!', 'HOME'),
 ('demo@cinemalog.app', 372058,  7, 5, 'the comet scene. that is all.', 'HOME'),
 ('demo@cinemalog.app', 426063,  5, 4, 'gorgeous and gross in equal measure.', 'HOME'),
 ('demo@cinemalog.app', 1417,    2, 5, 'the pale man lives in my nightmares now.', 'HOME'),
 ('demo@cinemalog.app', 4935,    2, 5, NULL, 'HOME'),
 ('demo@cinemalog.app', 157336,  1, 4, 'need a bigger screen.', 'CINEMA'),
 ('mochi@cinemalog.app', 129,   40, 5, 'every frame looks like a painting I want on my wall.', 'HOME'),
 ('mochi@cinemalog.app', 4935,  22, 4, 'pure comfort. like a warm blanket with a dragon in it.', 'HOME'),
 ('mochi@cinemalog.app', 372058, 9, 5, 'cried at a cartoon again. no regrets.', 'CINEMA'),
 ('nao@cinemalog.app',   493922, 60, 5, 'watched with all the lights on. still jumped twice.', 'HOME'),
 ('nao@cinemalog.app',   138843, 18, 4, 'the sound design did most of the damage, honestly.', 'CINEMA'),
 ('nao@cinemalog.app',   426063, 11, 4, 'slow first half, then it does not let go.', 'HOME'),
 ('toast@cinemalog.app', 496243, 33, 5, 'guessed wrong three times. loved it.', 'CINEMA'),
 ('toast@cinemalog.app', 129,    14, 4, 'made me want to go outside and look at clouds.', 'HOME'),
 ('toast@cinemalog.app', 157336,  6, 5, 'came for spaceships, left thinking about my life.', 'CINEMA')
) AS v(email, tmdb_id, days_ago, rating, review, place)
JOIN users u  ON u.email = v.email
JOIN movies m ON m.tmdb_id = v.tmdb_id;

INSERT INTO watchlist_items (user_id, movie_id)
SELECT u.id, m.id FROM users u JOIN movies m ON m.tmdb_id IN (10494, 396535, 670, 545611, 693134, 11104)
WHERE u.email = 'demo@cinemalog.app';

INSERT INTO liked_movies (user_id, movie_id)
SELECT u.id, m.id FROM users u JOIN movies m ON m.tmdb_id IN (129, 372058, 14836, 493922, 1417, 4935, 120467)
WHERE u.email = 'demo@cinemalog.app';

INSERT INTO collections (user_id, name, description)
SELECT u.id, v.name, v.description
FROM (VALUES
 ('Halloween Night', 'for the last week of October, blankets mandatory.'),
 ('Ghibli Weekend', 'saturday + sunday, back to back.'),
 ('Movies I Want to Cry To', 'tissues within reach.'),
 ('Date Night Movies', NULL),
 ('Rainy Sunday', 'still deciding what goes here.')
) AS v(name, description)
JOIN users u ON u.email = 'demo@cinemalog.app';

INSERT INTO collection_movies (collection_id, movie_id)
SELECT c.id, m.id
FROM (VALUES
 ('Halloween Night', 493922), ('Halloween Night', 138843), ('Halloween Night', 426063), ('Halloween Night', 530385),
 ('Ghibli Weekend', 129), ('Ghibli Weekend', 8392), ('Ghibli Weekend', 4935), ('Ghibli Weekend', 16859), ('Ghibli Weekend', 12429),
 ('Movies I Want to Cry To', 354912), ('Movies I Want to Cry To', 14160), ('Movies I Want to Cry To', 372058),
 ('Date Night Movies', 313369), ('Date Night Movies', 194), ('Date Night Movies', 76)
) AS v(name, tmdb_id)
JOIN users u ON u.email = 'demo@cinemalog.app'
JOIN collections c ON c.user_id = u.id AND c.name = v.name
JOIN movies m ON m.tmdb_id = v.tmdb_id;
