package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("InMemoryUserStorage") UserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAll();
    }

    public User createUser(User user) {
        log.debug("Создаем пользователя {}", user);
        if (isThereEmail(user)) {
            throw new ValidationBadRequestException("Пользователь с указанным email уже существует: " + user.getEmail());
        }
        validateUser(user);
        userStorage.addUser(user);
        return user;
    }

    public User putUser(User user) {
        log.debug("Обновляем пользователя {}", user);
        int userId = user.getId();
        User updatedUser = getUserById(userId);
        if (isThereEmail(user)) {
            throw new ValidationBadRequestException("Пользователь с указанным email уже существует: " + user.getEmail());
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
        User firstUser = getUserById(userId);
        User secondUser = getUserById(friendId);
        result.add(firstUser);
        result.add(secondUser);
        firstUser.getFriends().add(friendId);
        secondUser.getFriends().add(userId);
        userStorage.addToFriend(userId, friendId);
        return result;
    }

    public List<User> deleteFromFriend(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        User firstUser = getUserById(userId);
        User secondUser = getUserById(friendId);
        firstUser.getFriends().remove(friendId);
        secondUser.getFriends().remove(userId);
        result.add(firstUser);
        result.add(secondUser);
        return result;
    }

    public List<User> getUserFriends(int id) {
        return userStorage.getAllFriendsById(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        List<User> result = new ArrayList<>();
        Set<Integer> userFirst = getUserById(id).getFriends();
        Set<Integer> userSecond = getUserById(otherId).getFriends();
        Set<Integer> common = new HashSet<>(userFirst);
        common.retainAll(userSecond);
        for (Integer userId : common) {
            result.add(getUserById(userId));
        }
        return result;
    }

    public User getUserById(int id) {
        return userStorage.getUserByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + id));
    }

    public Boolean isThereEmail(User user) {
        String userEmail = user.getEmail();
        int userId = user.getId();
        boolean checkEmail = false;
        for (User userCheck : userStorage.getAll()) {
            if (userCheck.getEmail().equals(userEmail) && (userCheck.getId() != userId)) {
                checkEmail = true;
                break;
            }
        }
        return checkEmail;
    }
}