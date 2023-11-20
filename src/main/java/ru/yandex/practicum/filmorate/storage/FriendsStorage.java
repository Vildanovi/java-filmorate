package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserFriends;

import java.util.List;
import java.util.Set;

public interface FriendsStorage {

    List<UserFriends> getAllFriendsById(int id);

    Set<Integer> getUserFriends(int id);

    void addToFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    List<Integer> getCommonFriends(Integer userId, Integer friendId);
}
