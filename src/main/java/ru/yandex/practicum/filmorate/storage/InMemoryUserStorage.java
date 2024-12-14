package ru.yandex.practicum.filmorate.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
