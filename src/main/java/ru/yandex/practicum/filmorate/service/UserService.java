package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsersList() {
        return userStorage.getUsersList();
    }

    public User getUser(int id) {
        return userStorage.findUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriendsList(int curUserId) {
        return userStorage.getFriendsList(curUserId);
    }

    public List<User> getFriendsIntersect(int firstUserId, int secondUserId) {
        return userStorage.getFriendsIntersect(firstUserId, secondUserId);
    }

}
