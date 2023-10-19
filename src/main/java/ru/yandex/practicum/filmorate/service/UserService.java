package ru.yandex.practicum.filmorate.service;

// Создайте UserService, который будет отвечать за такие операции
// с пользователями, как добавление в друзья, удаление из друзей,
// вывод списка общих друзей. Пока пользователям не надо одобрять
// заявки в друзья — добавляем сразу. То есть если Лена стала
// другом Саши, то это значит, что Саша теперь друг Лены.

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {

    InMemoryUserStorage inMemoryUserStorage;
    private int uniqueId = 0;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAll();
    }

    public User createUser(User user) {
        String userEmail = user.getEmail();
        boolean checkEmail = false;
        for (User userCheck : inMemoryUserStorage.getAll()) {
            if (userCheck.getEmail().equals(userEmail)) {
                checkEmail = true;
                break;
            }
        }
        if (checkEmail) {
            throw new ValidationException("Пользователь с указанным email уже существует: " + userEmail);
        }
        validateUser(user);
        user.setId(getUniqueId());
        inMemoryUserStorage.addUser(user);
        log.debug("Создаем пользователя {}", user);
        return user;
    }

    public User putUser(User user) {
        log.debug("Обновляем пользователя {}", user);
        validateUser(user);
        int userId = user.getId();
        User updatedUser = inMemoryUserStorage.getUserByID(userId);
        if (updatedUser == null) {
            throw new ValidationException("Пользователь с указанным login не найден: " + userId);
        }
        if (!user.getName().equals(user.getLogin())) {
            updatedUser.setName(user.getName());
        }
        updatedUser.setEmail(user.getEmail());
        updatedUser.setLogin(user.getLogin());
        updatedUser.setBirthday(user.getBirthday());
        return updatedUser;
    }

    private void validateUser(User user) {
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> addToFriend(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        User firstUser = inMemoryUserStorage.getUserByID(userId);
        User secondUser = inMemoryUserStorage.getUserByID(friendId);
        result.add(firstUser);
        result.add(secondUser);
        firstUser.setFriends(Collections.singleton(friendId));
        secondUser.setFriends(Collections.singleton(userId));
        return result;
    }

    public List<User> deleteFromFriend(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        User firstUser = inMemoryUserStorage.getUserByID(userId);
        User secondUser = inMemoryUserStorage.getUserByID(friendId);
        if (firstUser == null && secondUser == null) {
            throw new ValidationException("Пользователи с указанными id не найдены: " + userId + " " + friendId);
        }
        if (firstUser == null) {
            throw new ValidationException("Пользователь с указанными id не найдены: " + userId);
        }
        if (secondUser == null) {
            throw new ValidationException("Пользователь с указанными id не найдены: " + friendId);
        }
        for (Integer friend : firstUser.getFriends()) {
            if (friend == friendId) {
                firstUser.getFriends().remove(friend);
            }
        }
        for (Integer friend : secondUser.getFriends()) {
            if (friend == userId) {
                firstUser.getFriends().remove(userId);
            }
        }
        result.add(firstUser);
        result.add(secondUser);
        return result;
    }

    public List<User> getUserFriends(int id) {
        return inMemoryUserStorage.getAllFriendsById(id);
    }

    public List<User> getCommonFriends() {
        return null;
    }

    public User getUserById(int id) {
        return inMemoryUserStorage.getUserByID(id);
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
