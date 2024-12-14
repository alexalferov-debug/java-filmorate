package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        logger.info("Добавление друга {}Для пользователя{}", friendId, userId);
        User currentUser = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        currentUser.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(int userId, int friendId) {
        logger.info("Удаление из списка друзей пользователя {} друга {}", userId, friendId);
        User curUser = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);
        curUser.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getFriendsList(int curUserId) {
        logger.info("Запрошен список друзей пользователя {}", curUserId);
        User curUser = userStorage.findUserById(curUserId);
        return userStorage.getUsersList()
                .stream()
                .filter(user -> curUser.getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getFriendsIntersect(int firstUserId, int secondUserId) {
        logger.info("Попытка получить пересечения друзей между пользователя с id {} и {}", firstUserId, secondUserId);
        Set<Integer> firstUserFriends = new HashSet<>(userStorage.findUserById(firstUserId).getFriends());
        Set<Integer> secondUserFriends = new HashSet<>(userStorage.findUserById(secondUserId).getFriends());
        firstUserFriends.retainAll(secondUserFriends);
        return userStorage.getUsersList()
                .stream()
                .filter(user -> firstUserFriends.contains(user.getId()))
                .collect(Collectors.toList());
    }

}
