package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmTest {
    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.usingContext().getValidator();
    }

    @Test
    public void shouldInvalidFilmWhenNameIsNull() {
        Film film = new Film();
        film.setName(null);
        film.setDescription("A valid description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(2, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldInvalidFilmWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Title");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldInvalidFilmWhenReleaseDateIsBefore1895() {
        Film film = new Film();
        film.setName("Title");
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.of(1894, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("Дата релиза должна быть не раньше 28 декабря 1895 года", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldInvalidFilmWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Title");
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        assertEquals("продолжительность фильма должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    public void shouldNotHaveViolationsForValidFilm() {
        Film film = new Film();
        film.setName("Title");
        film.setDescription("Some description");
        film.setReleaseDate(LocalDate.of(2024, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(0, violations.size());
    }
}

