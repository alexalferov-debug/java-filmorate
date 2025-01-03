MERGE INTO genre AS g
    USING (VALUES
               ('Комедия'),
               ('Драма'),
               ('Мультфильм'),
               ('Триллер'),
               ('Документальный'),
               ('Боевик')
    ) AS v(name)
    ON g.name = v.name
    WHEN NOT MATCHED THEN
        INSERT (name) VALUES (v.name);

MERGE INTO rating AS r
    USING (VALUES
               ('G','у фильма нет возрастных ограничений'),
               ('PG','детям рекомендуется смотреть фильм с родителями'),
               ('PG-13','детям до 13 лет просмотр не желателен'),
               ('R','лицам до 17 лет просматривать фильм можно только в присутствии взрослого'),
               ('NC-17','лицам до 18 лет просмотр запрещён')
    ) AS v(name,description)
    ON r.name = v.name
    WHEN NOT MATCHED THEN
        INSERT (name,description) VALUES (v.name,v.description);