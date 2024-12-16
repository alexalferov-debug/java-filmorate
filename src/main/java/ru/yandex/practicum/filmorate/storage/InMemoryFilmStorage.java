package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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

    private int generateId() {
        return idGenerator.incrementAndGet();
    }
}