package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.Set;

@Getter
public class FilmDTO {
    private final int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
    private final int likesCount;
    private final Set<Genre> genres;
    private final Mpa mpa;

    public FilmDTO(Film film) {
        this.id = film.getId();
        this.name = film.getName();
        this.description = film.getDescription();
        this.releaseDate = film.getReleaseDate();
        this.duration = film.getDuration();
        this.genres = film.getGenres();
        this.mpa = film.getMpa();
        this.likesCount = film.getLikesCount();
    }
}
