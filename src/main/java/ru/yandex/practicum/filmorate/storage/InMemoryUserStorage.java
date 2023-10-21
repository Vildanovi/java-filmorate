package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int uniqueId = 0;

    @Autowired
    public InMemoryUserStorage() {
    }

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
        return Optional.of(users.get(id));
    }

//    @Override
//    public User getUserByID(int id) {
//        return users.get(id);
//    }

    @Override
    public List<User> getAllFriendsById(int id) {
        List<User> list = new ArrayList<>();
        for (Integer userFriendId : users.get(id).getFriends()) {
            User user = users.get(userFriendId);
            list.add(user);
        }
        return list;
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
