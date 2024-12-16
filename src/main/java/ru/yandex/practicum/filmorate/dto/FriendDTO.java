package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Getter
public class FriendDTO {
    private final int id;
    private final String name;
    private final String email;
    private final String login;
    private final LocalDate birthday;

    public FriendDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.birthday = user.getBirthday();
    }
}
