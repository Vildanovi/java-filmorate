package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriends;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FriendsDbStorage implements FriendsStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public FriendsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllFriendsById(int id) {
        String userFriendsSql = "SELECT f2.id, f2.user1_id, f2.user2_id, f2.initiator_id, u.EMAIL, u.login, u.name, u.birthday " +
                "FROM user_friends as f2 " +
                "LEFT JOIN users u " +
                "ON f2.user2_id = u.id " +
                "WHERE f2.user1_id = ?";
        List<User> userFriends = jdbcTemplate.query(userFriendsSql, (rs, rowNum) -> makeUser(rs), id);
        if (userFriends.isEmpty()) {
            return Collections.emptyList();
        }
        return userFriends;
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("user2_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }

    @Override
    public Set<Integer> getUserFriends(int id) {
        String userFriendsSql = "SELECT f2.id, f2.user1_id, f2.user2_id, f2.initiator_id \n" +
                "FROM user_friends as f2\n" +
                "WHERE f2.user2_id = ?;";
        List<UserFriends> userFriends = jdbcTemplate.query(userFriendsSql, (rs, rowNum) -> makeFriends(rs), id);
        Set<Integer> friends = userFriends.stream()
                .map(UserFriends::getUser1Id)
                .collect(Collectors.toSet());
        if (friends.isEmpty()) {
            return Collections.emptySet();
        }
        return friends;
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

    @Override
    public List<User> getCommonFriends(Integer userId, Integer friendId) {
        String sqlCommon = "SELECT f1.user2_id, u.EMAIL, u.login, u.name, u.birthday " +
                "FROM user_friends AS f1 " +
                "LEFT JOIN users u " +
                "ON f1.user2_id = u.id " +
                "INNER JOIN user_friends AS f2 ON f1.user2_id = f2.user2_id " +
                "WHERE f1.user1_id = ? AND f2.user1_id = ?;";
        return jdbcTemplate.query(sqlCommon, (rs, rowNum) -> makeUser(rs), userId, friendId);
    }

    public UserFriends makeFriends(ResultSet rs) throws SQLException {
        return UserFriends.builder()
                .id(rs.getInt("id"))
                .user1Id(rs.getInt("user1_id"))
                .user2Id(rs.getInt("user2_id"))
                .initiatorId(rs.getInt("initiator_id"))
                .build();
    }
}
