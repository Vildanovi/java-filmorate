package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    void removeUser(int id);

    void updateUser(User user);

    List<User> getAll();

    Optional<User> getUserByID(int id);

    List<User> getAllFriendsById(int id);

    void addToFriend(Integer userId, Integer friendId);
}
