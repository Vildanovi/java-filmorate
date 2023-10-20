package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    void addUser(User user);

    void removeUser(int id);

    void updateUser(User user);

    List<User> getAll();

    User getUserByID(int id);

    List<User> getAllFriendsById(int id);
}
