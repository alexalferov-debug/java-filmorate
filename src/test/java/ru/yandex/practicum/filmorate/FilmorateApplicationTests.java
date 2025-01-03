package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.DatabaseValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.helpers.GenerateRandomizeInstances;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dao.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.dao.DbMpaStorage;
import ru.yandex.practicum.filmorate.storage.dao.DbUserStorage;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({DbUserStorage.class, DbFilmStorage.class, DbMpaStorage.class, DbGenreStorage.class})
class FilmoRateApplicationTests {
    private final DbUserStorage userStorage;
    private final DbFilmStorage filmStorage;
    private final DbMpaStorage mpaStorage;
    private final DbGenreStorage genreStorage;

    @Test
    @Description("Проверка, что добавленный пользователь аналогичен тому, что возвращается по запросу")
    public void testFindUserById() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        User foundUser = userStorage.findUserById(addedUser.getId());
        assertThat(foundUser).isEqualTo(addedUser);
    }

    @Test
    @Description("Проверка, что при запросе данных с некорректным ID выбрасыается исключение NotFoundException")
    public void testFindUserByIncorrectId() {
        assertThatThrownBy(() -> userStorage.findUserById(-1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id = -1 не найден");
    }

    @Test
    @Description("Проверка корректного возврата списка пользователей")
    public void testFindAllUsers() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        User anotherUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        List<User> users = userStorage.getUsersList();
        assertThat(users)
                .hasSize(2)
                .contains(addedUser, anotherUser);
    }

    @Test
    @Description("Проверка ошибки при запросе пустого списка пользователей")
    public void testFindAllUsersWithEmptyDb() {
        List<User> users = userStorage.getUsersList();
        assertThat(users).isEmpty();
    }

    @Test
    @Description("Проверим, что при добавлении пользователя добавляемый появляется в друзьях у добавляющего")
    public void testAddFriendToUser() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        User anotherUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        userStorage.addFriend(anotherUser.getId(), addedUser.getId());
        List<User> friends = userStorage.getFriendsList(anotherUser.getId());
        assertThat(friends)
                .contains(addedUser);
    }

    @Test
    @Description("Проверим, что при добавлении пользователя добавляющий не в друзьях у добавляемого")
    public void testUserNotInFriendsOfFriend() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        User anotherUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        userStorage.addFriend(anotherUser.getId(), addedUser.getId());
        List<User> friends = userStorage.getFriendsList(addedUser.getId());
        assertThat(friends)
                .doesNotContain(anotherUser);
    }

    @Test
    @Description("Проверим, что пользователь пропадает из друзей при удалении")
    public void testRemoveFriendFromUser() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        User anotherUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        userStorage.addFriend(anotherUser.getId(), addedUser.getId());
        userStorage.removeFriend(anotherUser.getId(), addedUser.getId());
        List<User> friends = userStorage.getFriendsList(anotherUser.getId());
        assertThat(friends)
                .doesNotContain(anotherUser);
    }

    @Test
    @Description("Проверим, что выбрасывается исключение при добавлении в друзья несуществующего пользователя")
    public void testThrowExceptionOnInvalidFriendAdded() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        assertThatThrownBy(() -> userStorage.addFriend(addedUser.getId(), 999999999))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Пользователь с id = 999999999 не найден");
    }

    @Test
    @Description("Проверим, что выбрасывается исключение при добавлении в друзья самого себя")
    public void testThrowExceptionOnAddedToFriendsItself() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        assertThatThrownBy(() -> userStorage.addFriend(addedUser.getId(), addedUser.getId()))
                .isInstanceOf(DatabaseValidationException.class)
                .hasMessageContaining("Невозможно добавить себя в качестве друга");
    }

    @Test
    @Description("Проверим, что выбрасывается исключение при удалении из друзей самого себя")
    public void testThrowExceptionOnDropFromFriendsItself() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        assertThatThrownBy(() -> userStorage.removeFriend(addedUser.getId(), addedUser.getId()))
                .isInstanceOf(DatabaseValidationException.class)
                .hasMessageContaining("Невозможно удалить себя из своих друзей");
    }

    @Test
    @Description("Проверим, что выбрасывается исключение при удалении из друзей пользователя, в друзья не добавленного")
    public void testThrowExceptionOnDropFromFriendsNotAddedUser() {
        User addedUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        User anotherUser = userStorage.addUser(GenerateRandomizeInstances.generateRandomUser());
        assertThatThrownBy(() -> userStorage.removeFriend(addedUser.getId(), anotherUser.getId()))
                .isInstanceOf(DatabaseValidationException.class)
                .hasMessageContaining("Пользователь с id " + anotherUser.getId() + " отсутствует в списке друзей");
    }

    @Test
    @Description("Добавляем корректный фильм")
    public void addFilm() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = genreStorage.getGenre(1);
        film.setGenres(Set.of(genre));
        Mpa mpa = mpaStorage.getMpa(1);
        film.setMpa(mpa);
        Film addedFilm = filmStorage.addFilm(film);
        assertThat(addedFilm)
                .isEqualTo(film);
    }

    @Test
    @Description("Добавляем фильм с двумя жанрами")
    public void addFilmWithTwoGenres() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = genreStorage.getGenre(1);
        Genre genre2 = genreStorage.getGenre(2);
        film.setGenres(Set.of(genre, genre2));
        Mpa mpa = mpaStorage.getMpa(1);
        film.setMpa(mpa);
        Film addedFilm = filmStorage.addFilm(film);
        assertThat(addedFilm)
                .isEqualTo(film);
    }

    @Test
    @Description("Добавляем фильм с несуществующим рейтингом")
    public void addFilmWithIncorrectMpa() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = genreStorage.getGenre(1);
        film.setGenres(Set.of(genre));
        Mpa mpa = new Mpa();
        mpa.setId(999);
        film.setMpa(mpa);
        assertThatThrownBy(() -> filmStorage.addFilm(film))
                .isInstanceOf(DatabaseValidationException.class)
                .hasMessageContaining("MPA с id 999 не найдено в базе данных.");
    }

    @Test
    @Description("Добавляем фильм с несуществующим жанром")
    public void addFilmWithIncorrectGenre() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = new Genre();
        genre.setId(999);
        film.setGenres(Set.of(genre));
        Mpa mpa = mpaStorage.getMpa(1);
        film.setMpa(mpa);
        assertThatThrownBy(() -> filmStorage.addFilm(film))
                .isInstanceOf(DatabaseValidationException.class)
                .hasMessageContaining("Жанр с id 999 не найден в базе данных.");
    }

    @Test
    public void testUpdateFilm() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = genreStorage.getGenre(1);
        film.setGenres(Set.of(genre));
        Mpa mpa = mpaStorage.getMpa(1);
        film.setMpa(mpa);
        film = filmStorage.addFilm(film);
        film.setName("Updated Film Name");

        Film updatedFilm = filmStorage.updateFilm(film);

        assertThat(updatedFilm).isNotNull();
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film Name");
    }

    @Test
    public void testGetFilm() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = genreStorage.getGenre(1);
        film.setGenres(Set.of(genre));
        Mpa mpa = mpaStorage.getMpa(1);
        film.setMpa(mpa);
        film = filmStorage.addFilm(film);
        Film getFilm = filmStorage.getFilm(film.getId());

        assertThat(getFilm).isNotNull();
        assertThat(film.getId()).isEqualTo(getFilm.getId());
    }

    @Test
    public void testGetFilm_NotFound() {
        assertThatThrownBy(() -> filmStorage.getFilm(-1))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Film not found with id: -1");
    }

    @Test
    public void testRemoveLike() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = genreStorage.getGenre(1);
        film.setGenres(Set.of(genre));
        Mpa mpa = mpaStorage.getMpa(1);
        film.setMpa(mpa);
        Film addedFilm = filmStorage.addFilm(film);
        Film thisFilm = filmStorage.getFilm(addedFilm.getId());
        int userId = 1;

        filmStorage.removeLike(thisFilm.getId(), userId);
        assertThat(thisFilm.getLikesCount()).isEqualTo(0);
    }

    @Test
    public void testAddFilmWithIncorrectMpa() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Genre genre = new Genre();
        genre.setId(1);
        film.setGenres(Set.of(genre));

        Mpa mpa = new Mpa();
        mpa.setId(999);
        film.setMpa(mpa);

        assertThatThrownBy(() -> filmStorage.addFilm(film))
                .isInstanceOf(DatabaseValidationException.class)
                .hasMessageContaining("MPA с id 999 не найдено в базе данных.");
    }

    @Test
    public void testAddFilmWithIncorrectGenre() {
        Film film = GenerateRandomizeInstances.generateRandomFilm();
        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(999);
        film.setGenres(Set.of(genre));

        assertThatThrownBy(() -> filmStorage.addFilm(film))
                .isInstanceOf(DatabaseValidationException.class)
                .hasMessageContaining("Жанр с id 999 не найден в базе данных.");
    }

}