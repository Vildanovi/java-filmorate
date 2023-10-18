package ru.yandex.practicum.filmorate.interfaces;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    void addUser(User user);
    void removeUser(int id);
    void updateUser(User user);
}
