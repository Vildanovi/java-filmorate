package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int uniqueId = 0;

    @Override
    public void addUser(User user) {
        user.setId(getUniqueId());
        users.put(user.getId(), user);
    }

    @Override
    public void removeUser(int id) {
        users.remove(id);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserByID(int id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> getAllFriendsById(int id) {
        return users.get(id).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
