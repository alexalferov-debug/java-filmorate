package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class FilmService {
    private final List<Film> films = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(FilmService.class);


    public Film addFilm(Film film) {
        logger.debug("Попытка добавления фильма: {}", film);
        film.setId(generateId());
        films.add(film);
        logger.info("Фильм добавлен с ID: {}", film.getId());
        return film;
    }

    public Film updateFilm(Film filmForUpdate) {
        logger.debug("Попытка обновления фильма с ID: {}", filmForUpdate.getId());
        if (films.stream().noneMatch(film -> film.getId() == filmForUpdate.getId())) {
            logger.error("Ошибка обновления фильма: фильм с ID {} не найден", filmForUpdate.getId());
            throw new NotFoundException("Film not found with id: " + filmForUpdate.getId());
        }
        films.replaceAll(film -> filmForUpdate.getId() == film.getId() ? filmForUpdate : film);
        logger.info("Фильм с ID {} обновлён", filmForUpdate.getId());
        return filmForUpdate;
    }

    public List<Film> getFilmsList() {
        logger.info("Получение списка фильмов");
        return films;
    }

    private int generateId() {
        return idGenerator.incrementAndGet();
    }
}