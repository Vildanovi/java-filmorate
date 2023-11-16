merge INTO genre(id, name)
    VALUES (1, 'Комедия'), (2, 'Драма'), (3, 'Мультфильм'), (4, 'Триллер'), (5, 'Документальный'), (6, 'Боевик');

-- INSERT INTO friends_status(name)
-- VALUES ('Неподтверждённая'),
--        ('Подтверждённая');

merge INTO film_mpa_rating(id, name)
VALUES (1, 'G'),
       (2, 'PG'),
       (3, 'PG-13'),
       (4, 'R'),
       (5, 'NC-17');


-- INSERT INTO users(id, email, login, name, birthday)
-- VALUES (1, '1mail@ya.ru', 'логин1', 'имя1', '2020-01-01'),
--        (2, '2mailз@ya.ru', 'логин2', 'имя2', '2020-01-01'),
--        (3, '3mail@ya.ru', 'логин3', 'имя3', '2020-01-01'),
--        (4, '4mail@ya.ru', 'логин4', 'имя4', '2020-01-01'),
--        (5, '5mail@ya.ru', 'логин5', 'имя5', '2020-01-01');
--
-- INSERT INTO user_friends(id, user1_id, user2_id, initiator_id)
-- VALUES (1, '1', '2', '1'),
--        (2, '1', '3', '1'),
--        (3, '3', '1', '1'),
--        (4, '1', '4', '4'),
--        (5, '4', '1', '4');
--
--
-- SELECT f2.user1_id
-- FROM user_friends as f2
--          JOIN (SELECT f.user2_id
--                FROM user_friends as f
--                         JOIN users as u ON f.user1_id = u.id
--                WHERE f.user1_id = 1) AS p
--               ON f2.user1_id = p.user2_id
-- WHERE f2.user2_id = 1;


-- SELECT f1.user2_id
-- FROM user_friends AS f1
--          INNER JOIN user_friends AS f2 ON f1.user2_id = f2.user2_id
-- WHERE f1.user1_id = 1 AND f2.user1_id = 4;

