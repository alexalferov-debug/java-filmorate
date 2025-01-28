package ru.yandex.practicum.filmorate.storage.ram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final List<User> users = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(InMemoryUserStorage.class);

    public InMemoryUserStorage() {
    }

    @Override
    public User findUserById(int userId) {
        return users.stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("User not found: {}", userId);
                    return new NotFoundException("User not found with id: " + userId);
                });
    }

    @Override
    public User addUser(User user) {
        logger.debug("Попытка добавления пользователя: {}", user);
        user.setId(generateId());
        setUsernameOnNull(user);
        users.add(user);
        logger.info("Пользователь добавлен с ID: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User userForUpdate) {
        logger.debug("Попытка обновления пользователя с ID: {}", userForUpdate.getId());
        User curUser = findUserById(userForUpdate.getId());
        setUsernameOnNull(userForUpdate);
        userForUpdate.setFriends(curUser.getFriends());
        users.replaceAll(user -> userForUpdate.getId() == user.getId() ? userForUpdate : user);
        logger.info("Пользователь с ID {} обновлён", userForUpdate.getId());
        return userForUpdate;
    }

    @Override
    public List<User> getUsersList() {
        logger.info("Получение списка пользователей");
        return users;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        logger.info("Добавление друга {}Для пользователя{}", friendId, userId);
        User currentUser = findUserById(userId);
        User friend = findUserById(friendId);
        currentUser.addFriend(friendId);
        friend.addFriend(userId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        logger.info("Удаление из списка друзей пользователя {} друга {}", userId, friendId);
        User curUser = findUserById(userId);
        User friend = findUserById(friendId);
        curUser.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    @Override
    public List<User> getFriendsList(int curUserId) {
        logger.info("Запрошен список друзей пользователя {}", curUserId);
        User curUser = findUserById(curUserId);
        return getUsersList()
                .stream()
                .filter(user -> curUser.getFriends().contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getFriendsIntersect(int firstUserId, int secondUserId) {
        logger.info("Попытка получить пересечения друзей между пользователя с id {} и {}", firstUserId, secondUserId);
        Set<Integer> firstUserFriends = new HashSet<>(findUserById(firstUserId).getFriends());
        Set<Integer> secondUserFriends = new HashSet<>(findUserById(secondUserId).getFriends());
        firstUserFriends.retainAll(secondUserFriends);
        return getUsersList()
                .stream()
                .filter(user -> firstUserFriends.contains(user.getId()))
                .collect(Collectors.toList());
    }

    private int generateId() {
        return idGenerator.incrementAndGet();
    }

    private void setUsernameOnNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            logger.debug("У пользователя с id {} не установлено имя, в качестве имени будет присвоен логин: {}", user.getId(), user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
