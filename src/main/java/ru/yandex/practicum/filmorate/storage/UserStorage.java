package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User findUserById(int userId);

    User addUser(User user);

    User updateUser(User user);

    List<User> getUsersList();

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<User> getFriendsList(int curUserId);

    List<User> getFriendsIntersect(int firstUserId, int secondUserId);
}
