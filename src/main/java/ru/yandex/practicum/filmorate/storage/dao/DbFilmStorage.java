package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.DatabaseValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.dao.mapper.GenreMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@Primary
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbc;
    private final FilmMapper filmMapper = new FilmMapper();
    private static final Logger logger = LoggerFactory.getLogger(DbFilmStorage.class);

    @Autowired
    public DbFilmStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    @Transactional
    public Film addFilm(Film film) {
        logger.debug("Попытка добавления фильма: {}", film);
        if (Objects.nonNull(film.getMpa()) && !isMpaExists(film.getMpa().getId())) {
            logger.warn("MPA с id {} не найдено в базе данных.", film.getMpa().getId());
            throw new DatabaseValidationException("MPA с id " + film.getMpa().getId() + " не найдено в базе данных.");
        }
        if (Objects.nonNull(film.getGenres()) && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (!isGenreExists(genre.getId())) {
                    logger.warn("Жанр с id {} не найден в базе данных.", genre.getId());
                    throw new DatabaseValidationException("Жанр с id " + genre.getId() + " не найден в базе данных.");
                }
            }
        }
        String filmInsertQuery =
                "INSERT INTO film (name, description, release_date, duration, rating_id) " +
                        "VALUES (?, ?, ?, ?, ?);";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rowsAffected = jdbc.update(
                con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(filmInsertQuery, new String[]{"id"});
                    preparedStatement.setString(1, film.getName());
                    preparedStatement.setString(2, film.getDescription());
                    preparedStatement.setDate(3, Date.valueOf(film.getReleaseDate()));
                    preparedStatement.setInt(4, film.getDuration());
                    if (Objects.nonNull(film.getMpa())) {
                        preparedStatement.setInt(5, film.getMpa().getId());
                    } else {
                        preparedStatement.setNull(5, java.sql.Types.INTEGER);
                    }
                    return preparedStatement;
                }, keyHolder
        );
        if (rowsAffected == 0) {
            logger.info("Запрос на добавление фильма вернул 0 строк, вероятно, добавление не произошло");
        }
        film.setId((int) keyHolder.getKey().longValue());
        if (Objects.nonNull(film.getGenres()) && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                String insertGenreLingQuery = "INSERT INTO film_genre_link values(?,?)";
                jdbc.update(insertGenreLingQuery, film.getId(), genre.getId());
            }
        }
        logger.info("Фильм добавлен с ID: {}", film.getId());
        return getFilm(film.getId());
    }

    public Film updateFilm(Film filmForUpdate) {
        logger.debug("Попытка обновления фильма с ID: {}", filmForUpdate.getId());
        getFilm(filmForUpdate.getId());
        String updateFilmQuery = "UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        jdbc.update(updateFilmQuery,
                filmForUpdate.getName(),
                filmForUpdate.getDescription(),
                Date.valueOf(filmForUpdate.getReleaseDate()),
                filmForUpdate.getDuration(),
                filmForUpdate.getMpa().getId(),
                filmForUpdate.getId());
        String deleteGenreLinksQuery = "DELETE FROM film_genre_link WHERE film_id = ?";
        jdbc.update(deleteGenreLinksQuery, filmForUpdate.getId());
        if (Objects.nonNull(filmForUpdate.getGenres()) && !filmForUpdate.getGenres().isEmpty()) {
            Set<Genre> genreList = getGenreList(filmForUpdate);
            for (Genre genre : genreList) {
                String insertGenreLinkQuery = "INSERT INTO film_genre_link (film_id, genre_id) VALUES (?, ?)";
                jdbc.update(insertGenreLinkQuery, filmForUpdate.getId(), genre.getId());
            }
        }

        logger.info("Фильм с ID {} обновлён", filmForUpdate.getId());
        return filmForUpdate;
    }

    @Override
    public List<Film> getFilmsList() {
        logger.info("Получение списка фильмов");
        String getFilmQuery = "SELECT f.*," +
                "R.ID as mpa_id, " +
                "R.NAME as mpa_name " +
                " FROM film f " +
                "LEFT JOIN PUBLIC.RATING R on R.ID = f.RATING_ID ";
        List<Film> filmList = jdbc.query(getFilmQuery, filmMapper);
        for (Film film : filmList) {
            film.setGenres(getGenreList(film));
        }
        return filmList.isEmpty() ? null : filmList;
    }

    @Override
    public Film getFilm(int id) {
        logger.info("Запрошена информация о фильме {}", id);
        String getFilmQuery = "SELECT f.*," +
                "R.ID as mpa_id, " +
                "R.NAME as mpa_name " +
                " FROM film f " +
                "LEFT JOIN PUBLIC.RATING R on R.ID = f.RATING_ID " +
                "WHERE f.id = ? " +
                "LIMIT 1;";
        List<Film> filmList = jdbc.query(getFilmQuery, filmMapper, id);
        if (filmList.isEmpty()) {
            throw new NotFoundException("Film not found with id: " + id);
        }
        filmList.getFirst().setGenres(getGenreList(filmList.getFirst()));
        return filmList.getFirst();
    }

    @Override
    public void addLike(int filmId, int userId) {
        getFilm(filmId);
        String addLikeQuery = "INSERT INTO LIKES(user_id, film_id) VALUES (?,?)";
        jdbc.update(addLikeQuery, userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        getFilm(filmId);
        String removeLikeQuery = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbc.update(removeLikeQuery, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int filmsCount) {
        String getPopularFilms = "SELECT f.*, " +
                "r.ID AS mpa_id, " +
                "r.NAME AS mpa_name, " +
                "COUNT(l.FILM_ID) AS likes_count " +
                "FROM LIKES l " +
                "RIGHT JOIN FILM f ON f.ID = l.FILM_ID " +
                "LEFT JOIN RATING r ON f.RATING_ID = r.ID " +
                "GROUP BY f.ID, f.NAME, f.RELEASE_DATE, f.DURATION, f.DESCRIPTION, r.ID, r.NAME " + // Добавляем все поля
                "ORDER BY likes_count DESC " +
                "LIMIT ?";
        return jdbc.query(getPopularFilms, filmMapper, filmsCount);
    }

    private boolean isMpaExists(int id) {
        String query = "SELECT COUNT(*) FROM rating WHERE id = ?";
        return Boolean.TRUE.equals(jdbc.query(query, ps -> ps.setInt(1, id), rs -> rs.next() && rs.getInt(1) > 0));
    }

    private boolean isGenreExists(int id) {
        String query = "SELECT COUNT(*) FROM genre WHERE id = ?";
        return Boolean.TRUE.equals(jdbc.query(query, ps -> ps.setInt(1, id), rs -> rs.next() && rs.getInt(1) > 0));
    }

    private Set<Genre> getGenreList(Film film) {
        String getGenreListForFilm = "SELECT g.* FROM FILM_GENRE_LINK fgl " +
                "JOIN genre g on fgl.genre_id = g.id " +
                " WHERE FILM_ID = ?" +
                "ORDER BY fgl.genre_id";
        List<Genre> genreList = jdbc.query(getGenreListForFilm, new GenreMapper(), film.getId());
        return new LinkedHashSet<>(genreList);
    }
}
