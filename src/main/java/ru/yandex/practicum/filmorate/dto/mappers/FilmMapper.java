package ru.yandex.practicum.filmorate.dto.mappers;

import ru.yandex.practicum.filmorate.dto.FilmDTO;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {
    public static FilmDTO toFilmDTO(Film film) {
        return new FilmDTO(film);
    }

    public static List<FilmDTO> toFilmDTOList(List<Film> films) {
        return films
                .stream()
                .map(FilmMapper::toFilmDTO)
                .collect(Collectors.toList());
    }
}
