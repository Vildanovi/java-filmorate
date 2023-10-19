package ru.yandex.practicum.filmorate.service;

// Создайте UserService, который будет отвечать за такие операции
// с пользователями, как добавление в друзья, удаление из друзей,
// вывод списка общих друзей. Пока пользователям не надо одобрять
// заявки в друзья — добавляем сразу. То есть если Лена стала
// другом Саши, то это значит, что Саша теперь друг Лены.

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserUnknownException;
import ru.yandex.practicum.filmorate.exceptions.UserUpdateException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.*;

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
        log.debug("Создаем пользователя {}", user);
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
        return user;
    }

    public User putUser(User user) {
        log.debug("Обновляем пользователя {}", user);
        int userId = user.getId();
        User updatedUser = inMemoryUserStorage.getUserByID(userId);
        if (updatedUser == null) {
            throw new UserUpdateException("Пользователь не существует: " + user);
        }
        validateUser(user);
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

    public List<User> addToFriend(Integer userId, Integer friendId) {
        List<User> result = new ArrayList<>();

        User firstUser = inMemoryUserStorage.getUserByID(userId);
        if (firstUser == null) {
            throw new UserUnknownException("Пользователь с указанным id не существует: " + userId);
        }
        User secondUser = inMemoryUserStorage.getUserByID(friendId);
        if (secondUser == null) {
            throw new UserUnknownException("Пользователь с указанным id не существует: " + friendId);
        }
        result.add(firstUser);
        result.add(secondUser);
        firstUser.getFriends().add(friendId);
        secondUser.getFriends().add(userId);
        return result;
    }

    public List<User> deleteFromFriend(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        User firstUser = inMemoryUserStorage.getUserByID(userId);
        User secondUser = inMemoryUserStorage.getUserByID(friendId);
        if (firstUser == null && secondUser == null) {
            throw new UserNotFoundException("Пользователи с указанными id не найдены: " + userId + " " + friendId);
        }
        if (firstUser == null) {
            throw new UserNotFoundException("Пользователь с указанными id не найдены: " + userId);
        }
        if (secondUser == null) {
            throw new UserNotFoundException("Пользователь с указанными id не найдены: " + friendId);
        }
        firstUser.getFriends().remove(friendId);
        secondUser.getFriends().remove(userId);
        result.add(firstUser);
        result.add(secondUser);
        return result;
    }

    public List<User> getUserFriends(int id) {
        return inMemoryUserStorage.getAllFriendsById(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        List<User> result = new ArrayList<>();
        if (inMemoryUserStorage.getUserByID(id) == null) {
            throw new UserUnknownException("Пользователь с указанным id не существует: " + id);
        }
        if (inMemoryUserStorage.getUserByID(otherId) == null) {
            throw new UserUnknownException("Пользователь с указанным id не существует: " + otherId);
        }
        Set<Integer> userFirst = inMemoryUserStorage.getUserByID(id).getFriends();
        Set<Integer> userSecond = inMemoryUserStorage.getUserByID(otherId).getFriends();
//        boolean haveCommon = userFirst.containsAll(userSecond);
//        if (haveCommon) {
//            return result;
//        }
        Set<Integer> common = new HashSet<>(userFirst);
        common.retainAll(userSecond);
        for (Integer friend : common) {
            result.add(inMemoryUserStorage.getUserByID(friend));
        }
        return result;
    }

    public User getUserById(int id) {
        User result = inMemoryUserStorage.getUserByID(id);
        if (result == null) {
            throw new UserUnknownException("Пользователь с указанным id не найден: " + id);
        }
        return result;
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
