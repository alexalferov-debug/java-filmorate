package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserService userService;

    @Autowired
    FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film getFilmById(int id) {
        return filmStorage.getFilm(id);
    }

    public void addLike(int filmId, int userId) {
        userService.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        film.addLikedUser(userId);
    }

    public void removeLike(int filmId, int userId) {
        userService.getUser(userId);
        Film film = filmStorage.getFilm(filmId);
        film.removeLikedUser(userId);
    }

    public List<Film> getMostLikedFilms(int filmsCount) {
        return filmStorage
                .getFilmsList()
                .stream()
                .sorted((film, film1) -> Integer.compare(film1.getLikesCount(), film.getLikesCount()))
                .limit(filmsCount)
                .toList();
    }


}
