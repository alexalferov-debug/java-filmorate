package ru.yandex.practicum.filmorate.dto;

import lombok.Getter;
import ru.yandex.practicum.filmorate.dto.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class UserDTO {
    private final int id;
    private final String name;
    private final String email;
    private final String login;
    private final LocalDate birthday;
    private final List<FriendDTO> friends;

    public UserDTO(User user, List<User> friends) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.birthday = user.getBirthday();
        this.friends = Objects.isNull(friends) ? new ArrayList<>() : UserMapper.toFriendDTOs(friends);
    }
}
