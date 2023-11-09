package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFriends;
import ru.yandex.practicum.filmorate.storage.UserFriendsDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserFriendsDaoImpl implements UserFriendsDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserFriendsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<Integer> getUserFriends(int id) {
        String userFriendsSql = "SELECT f2.user1_id " +
                "FROM user_friends as f2 " +
                "JOIN (SELECT f.user2_id " +
                "FROM user_friends as f " +
                "JOIN users as u ON f.user1_id = u.id " +
                "WHERE f.user1_id = ?) AS p " +
                "ON f2.user1_id = p.user2_id " +
                "WHERE f2.user2_id = ?";
//        String userFriendsSql = "SELECT f.user2_id FROM user_friends " +
//                "as f JOIN users as u ON f.user2_id = u.id where f.user1_id = ?";
        List<UserFriends> userFriends = jdbcTemplate.query(userFriendsSql, (rs, rowNum) -> makeFriends(rs), id, id);

        Set<Integer> friends = userFriends.stream()
                .map(UserFriends::getUser2Id)
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

}
