package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@Slf4j
@Service
public class UserDbService {

    private final UserStorage userDbStorage;
    private final FriendsStorage friendsStorage;

    @Autowired
    public UserDbService(UserStorage userStorage, FriendsStorage friendsStorage) {
        this.userDbStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public List<User> getAllUsersDb() {
        return userDbStorage.getAll();
    }

    public User createUserDb(User user) {
        log.debug("Создаем пользователя {}", user);
        if (isThereEmailDb(user)) {
            throw new ValidationBadRequestException("Пользователь с указанным email уже существует: " + user.getEmail());
        }
        validateUser(user);
        return userDbStorage.addUser(user);
    }

    public User putUserDb(User user) {
        log.debug("Обновляем пользователя {}", user);
        int userId = user.getId();
        User updatedUser = userDbStorage.getUserByID(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден:"));
        if (isThereEmailDb(user)) {
            throw new UserUpdateException("Пользователь с указанным email уже существует: " + user.getEmail());
        }
        validateUser(user);
        if (!user.getName().equals(user.getLogin())) {
            user.setName(user.getName());
        }
        if (updatedUser != null) {
            userDbStorage.updateUser(user);
        } else {
            throw new EntityNotFoundException("Пользователь с указанным id не найден: " + userId);
        }
        return user;
    }

    public User removeUserByIdDB(int id) {
        User user = userDbStorage.getUserByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + id));
        userDbStorage.removeUser(id);
        return user;
    }

    public List<User> addToFriendDb(Integer userId, Integer friendId) {
        List<User> result = new ArrayList<>();
        result.add(getUserByIdDb(userId));
        result.add(getUserByIdDb(friendId));
        friendsStorage.addToFriend(userId, friendId);
        return result;
    }

    public List<User> deleteFromFriendDb(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        result.add(getUserByIdDb(userId));
        result.add(getUserByIdDb(friendId));
        friendsStorage.deleteFriend(userId, friendId);
        return result;
    }

    public List<User> getUserFriendsDb(int id) {
        return friendsStorage.getAllFriendsById(id);
    }

    public List<User> getCommonFriendsDb(int id, int otherId) {
        getUserByIdDb(id);
        getUserByIdDb(otherId);
        return friendsStorage.getCommonFriends(id, otherId);
    }

    public User getUserByIdDb(int id) {
        return userDbStorage.getUserByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + id));
    }

    public Boolean isThereEmailDb(User user) {
        String email = user.getEmail();
        int userId = user.getId();
        boolean checkEmail = false;
        for (User userCheck : userDbStorage.getAll()) {
            if (userCheck.getEmail().equals(email) && (userCheck.getId() != userId)) {
                checkEmail = true;
                break;
            }
        }
        return checkEmail;
    }

    private void validateUser(User user) {
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
