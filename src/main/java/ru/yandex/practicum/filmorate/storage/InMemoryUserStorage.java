package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
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
    public User getUserByID(int id) {
        return users.get(id);
    }

    public List<User> getAllFriendsById(int id) {
        List<User> list = new ArrayList<>();
        for (Integer userFriendId : users.get(id).getFriends()) {
            User user = users.get(userFriendId);
            list.add(user);
        }
        return list;
    }
}
