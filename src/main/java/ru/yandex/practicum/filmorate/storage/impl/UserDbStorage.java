package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserFriendsDao;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final UserFriendsDao userFriendsDao;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, UserFriendsDao userFriendsDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.userFriendsDao = userFriendsDao;
    }

    @Override
    public void addUser(User user) {
            String userAddSql = "insert into users(email, login, name, birthday) " +
                    "values (?, ?, ?, ?)";
            jdbcTemplate.update(userAddSql,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday()
            );
    }

    @Override
    public void removeUser(int id) {

    }

    @Override
    public void updateUser(User user) {

    }

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, (rs, rowNum) -> makeUser(rs));
    }

    private User makeUser(ResultSet rs) throws SQLException {
        // используем конструктор, методы ResultSet
        // и готовое значение user
        int userId = rs.getInt("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        Set<Integer> friends = userFriendsDao.getUserFriends(userId);
        return new User(userId, email, login, name, birthday, friends);
    }


    @Override
    public Optional<User> getUserByID(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from USERS where id = ?", id);
        Set<Integer> friends = userFriendsDao.getUserFriends(id);
        if(userRows.next()) {
            LocalDate birthday = Objects.requireNonNull(userRows.getDate("birthday")).toLocalDate();
            User user = new User();
            user.setId(userRows.getInt("id"));
            user.setEmail(userRows.getString("email"));
            user.setLogin(userRows.getString("login"));
            user.setName(userRows.getString("name"));
            user.setBirthday(birthday);
            user.setFriends(friends);

            log.info("Найдет пользователь: {} {}", userRows.getInt("id"), userRows.getString("name"));
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllFriendsById(int id) {
        return null;
    }
}
