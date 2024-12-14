package ru.yandex.practicum.filmorate.dto.mappers;

import ru.yandex.practicum.filmorate.dto.FriendDTO;
import ru.yandex.practicum.filmorate.dto.UserDTO;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserMapper {
    public static UserDTO toUserDTO(User user, List<User> friends) {
        return new UserDTO(user, friends);
    }

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user, null);
    }

    public static List<UserDTO> toUserDTOs(List<User> users, List<List<User>> friendsLists) {
        return users.stream()
                .map(user -> {
                    List<User> friends = friendsLists.stream()
                            .filter(friendsList -> friendsList.contains(user))
                            .findFirst()
                            .orElse(List.of());
                    return toUserDTO(user, friends);
                })
                .collect(Collectors.toList());
    }

    public static FriendDTO toFriendDTO(User user) {
        return new FriendDTO(user);
    }

    public static List<FriendDTO> toFriendDTOs(List<User> users) {
        return users
                .stream()
                .map(UserMapper::toFriendDTO)
                .collect(Collectors.toList());
    }
}
