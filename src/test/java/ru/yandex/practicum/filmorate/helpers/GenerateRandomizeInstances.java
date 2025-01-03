package ru.yandex.practicum.filmorate.helpers;

import net.datafaker.Faker;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

public class GenerateRandomizeInstances {
    static final Faker faker = new Faker(Locale.of("ru"));

    public static User generateRandomUser() {
        User user = new User();
        user.setName(faker.name().firstName());
        user.setLogin(faker.funnyName().name());
        user.setEmail(faker.internet().emailAddress());
        user.setBirthday(faker.timeAndDate().birthday(1,100));
        return user;
    }

    public static Film generateRandomFilm() {
        Film film = new Film();
        film.setName(faker.name().firstName());
        film.setDescription(faker.lorem().sentence());
        film.setDuration(faker.number().numberBetween(1, 100));
        film.setReleaseDate(LocalDate.ofInstant(faker.timeAndDate().past(), ZoneId.systemDefault()));
        return film;
    }
}
