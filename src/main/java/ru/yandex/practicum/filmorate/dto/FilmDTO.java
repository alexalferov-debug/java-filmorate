package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Getter
public class FilmDTO {
    private final int id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
    private final int likesCount;

    public FilmDTO(Film film) {
        this.id = film.getId();
        this.name = film.getName();
        this.description = film.getDescription();
        this.releaseDate = film.getReleaseDate();
        this.duration = film.getDuration();
        this.likesCount = film.getLikesCount();
    }
}
