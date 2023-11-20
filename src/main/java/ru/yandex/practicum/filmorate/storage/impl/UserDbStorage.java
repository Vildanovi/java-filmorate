package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("users")
                .usingColumns("email", "login", "name", "birthday")
                .usingGeneratedKeyColumns("id")
                .executeAndReturnKeyHolder(Map.of("email", user.getEmail(),
                        "login", user.getLogin(),
                        "name", user.getName(),
                        "birthday", user.getBirthday()))
                .getKeys();
        user.setId((Integer) keys.get("ID"));
            return user;
    }

    @Override
    public void removeUser(int id) {
        String sqlQuery = "delete from USERS where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public void updateUser(User user) {
        String userUpdateSql = "update users set email = ?, login = ?, name = ?, birthday = ? where id = ?";
        jdbcTemplate.update(userUpdateSql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
    }

    @Override
    public List<User> getAll() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, this::makeUser);
    }

    @Override
    public Optional<User> getUserByID(int id) {
        String sqlQuery = "select * from USERS where id = ?";
        return Optional.of(jdbcTemplate.query(sqlQuery, this::makeUser, id).stream().findAny())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + id));
    }

    @Override
    public List<User> getAllFriendsById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addToFriend(Integer userId, Integer friendId) {
        throw new UnsupportedOperationException();
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .friends(new HashSet<>())
                .build();
    }
}
