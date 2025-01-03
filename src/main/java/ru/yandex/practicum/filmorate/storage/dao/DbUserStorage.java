package ru.yandex.practicum.filmorate.storage.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.DatabaseValidationException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.mapper.UserMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Primary
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbc;
    private static final Logger logger = LoggerFactory.getLogger(DbUserStorage.class);

    @Autowired
    public DbUserStorage(final JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public User findUserById(int userId) {
        logger.info("Запрошена информация о пользователе с id={}", userId);
        String getUser = "select * from users where id = ? LIMIT 1";
        List<User> users = jdbc.query(getUser, new UserMapper(), userId);
        if (users.isEmpty()) {
            logger.warn("Пользователь с id = {} не найден", userId);
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
        return users.getFirst();
    }

    @Override
    public User addUser(User user) {
        logger.debug("Попытка добавления пользователя: {}", user);
        String addUserQuery = "insert into users (name, email, birthday,login) values (?, ?, ?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int affectedRows = jdbc.update(
                con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(addUserQuery, new String[]{"id"});
                    preparedStatement.setString(1, user.getName());
                    preparedStatement.setString(2, user.getEmail());
                    preparedStatement.setDate(3, Date.valueOf(user.getBirthday()));
                    preparedStatement.setString(4, user.getLogin());
                    return preparedStatement;
                }, keyHolder
        );
        if (affectedRows == 0) {
            logger.warn("При создании пользователя обновлено ноль строк, вероятно вставка не удалась");
        }
        user.setId(keyHolder.getKey().intValue());
        logger.info("Пользователь добавлен с ID: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        User userForUpdate = findUserById(user.getId());
        String updateUserQuery = "update users set name = ?, email = ?, birthday = ?, login = ? where id = ?";
        jdbc.update(updateUserQuery, user.getName(), user.getEmail(), user.getBirthday(), user.getLogin(), user.getId());
        return findUserById(user.getId());
    }

    @Override
    public List<User> getUsersList() {
        String sql = "select * from users";
        return jdbc.query(sql, new UserMapper());
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new DatabaseValidationException("Невозможно добавить себя в качестве друга");
        }
        User friend = findUserById(friendId);
        User user = findUserById(userId);
        String addFriendToUser = "insert into FRIENDS_LINK (USER_ID, FRIEND_ID) values (?, ?)";
        jdbc.update(addFriendToUser, user.getId(), friend.getId());
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new DatabaseValidationException("Невозможно удалить себя из своих друзей");
        }
        User friend = findUserById(friendId);
        User user = findUserById(userId);
        if (!getFriendsList(userId).contains(friend)) {
            throw new DatabaseValidationException("Пользователь с id " + friendId + " отсутствует в списке друзей");
        }
        String removeFriendFromUser = "delete from FRIENDS_LINK where USER_ID = ? and FRIEND_ID = ?";
        jdbc.update(removeFriendFromUser, user.getId(), friend.getId());
    }

    @Override
    public List<User> getFriendsList(int curUserId) {
        User curUser = findUserById(curUserId);
        String getFriendsListQuery = "select * from users u " +
                "left join friends_link fl on u.ID = fl.friend_id" +
                " where fl.user_id = ?";
        return jdbc.query(getFriendsListQuery, new UserMapper(), curUserId);
    }

    @Override
    public List<User> getFriendsIntersect(int firstUserId, int secondUserId) {
        User firstUser = findUserById(firstUserId);
        User secondUser = findUserById(secondUserId);
        String findFriendsIntersectQuery = "SELECT DISTINCT u.* " +
                "FROM USERS u " +
                "         JOIN FRIENDS_LINK fl ON u.ID = fl.FRIEND_ID " +
                "WHERE fl.USER_ID IN (?, ?) " +
                "GROUP BY u.ID " +
                "HAVING COUNT(DISTINCT fl.USER_ID) = 2;";
        return jdbc.query(findFriendsIntersectQuery, new UserMapper(), firstUserId, secondUserId);
    }
}
