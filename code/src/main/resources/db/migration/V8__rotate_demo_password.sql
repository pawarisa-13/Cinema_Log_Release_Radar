-- Rotate the demo account password. The old one was published in V6, the README and the login page.
-- The new password is NOT stored in this repository; the team shares it privately.

UPDATE users
SET password_hash = '$2a$10$nQNc25OwVzdmjztXejLlc.XhSun/itkJFHuB9sHZdCmluhl/VqPk2'
WHERE email IN ('demo@cinemalog.app', 'mochi@cinemalog.app', 'nao@cinemalog.app', 'toast@cinemalog.app');
