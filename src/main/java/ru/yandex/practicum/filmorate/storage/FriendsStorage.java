package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface FriendsStorage {

    public List<User> getAllFriendsById(int id);

    Set<Integer> getUserFriends(int id);

    public void addToFriend(Integer userId, Integer friendId);

    public void deleteFriend(Integer userId, Integer friendId);

    public List<User> getCommonFriends(Integer userId, Integer friendId);
}
