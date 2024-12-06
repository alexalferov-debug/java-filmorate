package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private static final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService() {
    }

    public User addUser(User user) {
        logger.debug("Попытка добавления пользователя: {}", user);
        user.setId(generateId());
        setUsernameOnNull(user);
        users.add(user);
        logger.info("Пользователь добавлен с ID: {}", user.getId());
        return user;
    }

    public User updateUser(User userForUpdate) {
        logger.debug("Попытка обновления пользователя с ID: {}", userForUpdate.getId());
        if (users.stream().noneMatch(user1 -> user1.getId() == userForUpdate.getId())) {
            logger.error("Ошибка обновления пользователя: пользователь с ID {} не найден", userForUpdate.getId());
            throw new NotFoundException("User not found with id: " + userForUpdate.getId());
        }
        setUsernameOnNull(userForUpdate);
        users.replaceAll(user -> userForUpdate.getId() == user.getId() ? userForUpdate : user);
        logger.info("Пользователь с ID {} обновлён", userForUpdate.getId());
        return userForUpdate;
    }

    public List<User> getUsersList() {
        logger.info("Получение списка пользователей");
        return users;
    }

    private int generateId() {
        return idGenerator.incrementAndGet();
    }

    private void setUsernameOnNull(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            logger.debug("У пользователя с id " + user.getId() + " не установлено имя, в качестве имени будет присвоен логин: " + user.getLogin());
            user.setName(user.getLogin());
        }
    }
}
