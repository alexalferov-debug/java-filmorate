package ru.yandex.practicum.filmorate.storage.ram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final List<Film> films = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    @Override
    public Film addFilm(Film film) {
        logger.debug("Попытка добавления фильма: {}", film);
        film.setId(generateId());
        films.add(film);
        logger.info("Фильм добавлен с ID: {}", film.getId());
        return film;
    }

    @Override
    public Film updateFilm(Film filmForUpdate) {
        logger.debug("Попытка обновления фильма с ID: {}", filmForUpdate.getId());
        Film currentFilm = getFilm(filmForUpdate.getId());
        filmForUpdate.setLikedUsers(currentFilm.getLikedUsers());
        films.replaceAll(film -> filmForUpdate.getId() == film.getId() ? filmForUpdate : film);
        logger.info("Фильм с ID {} обновлён", filmForUpdate.getId());
        return filmForUpdate;
    }

    @Override
    public List<Film> getFilmsList() {
        logger.info("Получение списка фильмов");
        return films;
    }

    @Override
    public Film getFilm(int id) {
        logger.info("Поиск фильма с ID: {}", id);
        return films
                .stream()
                .filter(film -> film.getId() == id)
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Фильм не найден с ID: {}", id);
                    return new NotFoundException("Film not found with id: " + id);
                });
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        film.addLikedUser(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        film.removeLikedUser(userId);
    }

    @Override
    public List<Film> getPopularFilms(int filmsCount) {
        return getFilmsList()
                .stream()
                .sorted((film, film1) -> Integer.compare(film1.getLikesCount(), film.getLikesCount()))
                .limit(filmsCount)
                .toList();
    }

    private int generateId() {
        return idGenerator.incrementAndGet();
    }
}