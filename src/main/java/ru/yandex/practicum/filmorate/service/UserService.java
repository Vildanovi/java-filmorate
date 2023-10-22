package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.*;

@Slf4j
@Service
public class UserService {

    private final UserStorage inMemoryUserStorage;

    @Autowired
    public UserService(UserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public List<User> getAllUsers() {
        return inMemoryUserStorage.getAll();
    }

    public User createUser(User user) {
        log.debug("Создаем пользователя {}", user);


//        String userEmail = user.getEmail();
//        boolean checkEmail = false;
//        for (User userCheck : inMemoryUserStorage.getAll()) {
//            if (userCheck.getEmail().equals(userEmail)) {
//                checkEmail = true;
//                break;
//            }
//        }
        if (isThereEmail(user)) {
            throw new ValidationBadRequestException("Пользователь с указанным email уже существует: " + user.getEmail());
        }
        validateUser(user);
        inMemoryUserStorage.addUser(user);
        return user;
    }

    public User putUser(User user) {
        log.debug("Обновляем пользователя {}", user);
        int userId = user.getId();
//        User updatedUser = inMemoryUserStorage.getUserByID(userId).orElseThrow(() -> new UserUpdateException("Пользователь не существует: " + user));
        User updatedUser = getUserById(userId);

//        String userEmail = user.getEmail();
//        boolean checkEmail = false;
//        for (User userCheck : inMemoryUserStorage.getAll()) {
//            if (userCheck.getEmail().equals(userEmail) && (userCheck.getId() != userId)) {
//                checkEmail = true;
//                break;
//            }
//        }
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
//        User firstUser = inMemoryUserStorage.getUserByID(userId)
//                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не существует: " + userId));
//        User secondUser = inMemoryUserStorage.getUserByID(friendId)
//                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не существует: " + friendId));
        User firstUser = getUserById(userId);
        User secondUser = getUserById(friendId);
        result.add(firstUser);
        result.add(secondUser);
        firstUser.getFriends().add(friendId);
        secondUser.getFriends().add(userId);
        return result;
    }

    public List<User> deleteFromFriend(int userId, int friendId) {
        List<User> result = new ArrayList<>();
//        User firstUser = inMemoryUserStorage.getUserByID(userId)
//                .orElseThrow(() -> new ValidationBadRequestException("Пользователи с указанными id не найдены: " + userId));
//        User secondUser = inMemoryUserStorage.getUserByID(friendId)
//                .orElseThrow(() -> new ValidationBadRequestException("Пользователь с указанными id не найдены: " + friendId));
        User firstUser = getUserById(userId);
        User secondUser = getUserById(friendId);
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
//        Set<Integer> userFirst = inMemoryUserStorage.getUserByID(id)
//                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не существует: " + id)).getFriends();
        Set<Integer> userFirst = getUserById(id).getFriends();
//        Set<Integer> userSecond = inMemoryUserStorage.getUserByID(otherId)
//                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не существует: " + otherId)).getFriends();
        Set<Integer> userSecond = getUserById(otherId).getFriends();
        Set<Integer> common = new HashSet<>(userFirst);
        common.retainAll(userSecond);
        for (Integer userId : common) {
            result.add(getUserById(userId));
//            result.add(inMemoryUserStorage.getUserByID(userId)
//                    .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не существует: " + userId)));
        }
        return result;
    }

    public User getUserById(int id) {
        return inMemoryUserStorage.getUserByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + id));
    }

    public Boolean isThereEmail(User user) {
        String userEmail = user.getEmail();
        int userId = user.getId();
        boolean checkEmail = false;
        for (User userCheck : inMemoryUserStorage.getAll()) {
            if (userCheck.getEmail().equals(userEmail) && (userCheck.getId() != userId)) {
                checkEmail = true;
                break;
            }
        }
        return checkEmail;
    }
}
