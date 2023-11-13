package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriends;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("UserDbStorage")
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUser(User user) {
            String userAddSql = "insert into users(id, email, login, name, birthday) " +
                    "values (?, ?, ?, ?, ?)";
            jdbcTemplate.update(userAddSql,
                    user.getId(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday()
            );
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

    private User makeUser(ResultSet resultSet, int rowNum) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setEmail(resultSet.getString("email"));
        user.setLogin(resultSet.getString("login"));
        user.setName(resultSet.getString("name"));
        user.setBirthday(resultSet.getDate("birthday").toLocalDate());
        user.setFriends(getUserFriends(user.getId()));
        return user;
    }

    @Override
    public Optional<User> getUserByID(int id){
        String sqlQuery = "select * from USERS where id = ?";
        User user = jdbcTemplate.queryForObject(sqlQuery, this::makeUser, id);
        if(user != null) {
            log.info("Найден пользователь: {} {}", user.getId(), user.getName());
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public List<User> getAllFriendsById(int id) {
        String userFriendsSql = "SELECT f2.id, f2.user1_id, f2.user2_id, f2.initiator_id \n" +
                "FROM user_friends as f2\n" +
                "WHERE f2.user1_id = ?;";
        List<UserFriends> userFriends = jdbcTemplate.query(userFriendsSql, (rs, rowNum) -> makeFriends(rs), id);
        List<User> friends = userFriends.stream()
                .map(UserFriends::getUser2Id)
                .map(this::getUserByID)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        if(friends.isEmpty()) {
            return Collections.emptyList();
        }
        return friends;
    }

    public Set<Integer> getUserFriends(int id) {
        String userFriendsSql = "SELECT f2.id, f2.user1_id, f2.user2_id, f2.initiator_id \n" +
                "FROM user_friends as f2\n" +
                "WHERE f2.user2_id = ?;";
        List<UserFriends> userFriends = jdbcTemplate.query(userFriendsSql, (rs, rowNum) -> makeFriends(rs), id);
        Set<Integer> friends = userFriends.stream()
                .map(UserFriends::getUser1Id)
                .collect(Collectors.toSet());
        if(friends.isEmpty()) {
            return Collections.emptySet();
        }
        return friends;
    }

    public UserFriends makeFriends(ResultSet rs) throws SQLException {
        return new UserFriends(rs.getInt("id"), rs.getInt("user1_id"),
                rs.getInt("user2_id"), rs.getInt("initiator_id"));
    }

    @Override
    public void addToFriend(Integer userId, Integer friendId) {
        String sqlQuery = "insert into user_friends(user1_id, user2_id, initiator_id) values (?, ?, ?)";
        jdbcTemplate.update(sqlQuery, userId, friendId, userId);
        log.info("Пользователь c id {} в друзьях у id {}", userId, friendId);
    }

    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        String sqlDeleteFriend = "DELETE FROM user_friends WHERE user1_id = ? AND user2_id = ?";
        jdbcTemplate.update(sqlDeleteFriend, userId, friendId);
        log.info("Пользователь c id {} удален из друзей у id {}", userId, friendId);
    }

    public List<User> getCommonFriends(Integer userId, Integer friendId){
        SqlRowSet sqlCommon = jdbcTemplate.queryForRowSet("SELECT f1.user2_id " +
                "FROM user_friends AS f1 " +
                "INNER JOIN user_friends AS f2 ON f1.user2_id = f2.user2_id " +
                "WHERE f1.user1_id = ? AND f2.user1_id = ?;", userId, friendId);
        List<User> filmPopular = new ArrayList<>();
        while (sqlCommon.next()) {
            int id = sqlCommon.getInt("user2_id");
            filmPopular.add(getUserByID(id)
                    .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id)));
        }
        return filmPopular;
//        String sqlCommon = "SELECT f1.id, f1.user1_id, f1.user2_id, f1.initiator_id " +
//                "FROM user_friends AS f1 " +
//                "INNER JOIN user_friends AS f2 ON f1.user2_id = f2.user2_id " +
//                "WHERE f1.user1_id = ? AND f2.user1_id = ?;";
//        List<User> commonUsers = jdbcTemplate.query(sqlCommon, (this::makeUser), userId, friendId);
//        if(commonUsers.isEmpty()) {
//            return Collections.emptyList();
//        }
//        return commonUsers;
    }
}
