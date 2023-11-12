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
public class UserDbService {

    private int uniqueId = 0;
    private final UserStorage userDbStorage;

    @Autowired
    public UserDbService(@Qualifier("UserDbStorage") UserStorage userStorage) {
        this.userDbStorage = userStorage;
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
        user.setId(getUniqueId());
        userDbStorage.addUser(user);
        //Добавление друзей
//        user.setFriends(userDbStorage.getUserFriends(user.getId()));
        return user;
    }

    public User putUserDb(User user) {
        log.debug("Обновляем пользователя {}", user);
        int userId = user.getId();
        Optional<User> updatedUser = userDbStorage.getUserByID(userId);
        if (isThereEmailDb(user)) {
            throw new ValidationBadRequestException("Пользователь с указанным email уже существует: " + user.getEmail());
        }
        validateUser(user);
        if (!user.getName().equals(user.getLogin())) {
            user.setName(user.getName());
        }
        if(updatedUser.isPresent()) {
            userDbStorage.updateUser(user);
        } else {
            throw new ValidationBadRequestException("Пользователь с указанным id не найден: " + userId);
        }
        return user;
    }

    public List<User> addToFriendDb(Integer userId, Integer friendId) {
        List<User> result = new ArrayList<>();
        Optional<User> askFriendship = Optional.ofNullable(getUserByIdDb(userId));
        Optional<User> friend = Optional.ofNullable(getUserByIdDb(friendId));
        if(askFriendship.isPresent()) {
            result.add(getUserByIdDb(userId));
            if(friend.isPresent()) {
                userDbStorage.addToFriend(userId, friendId);
                result.add(getUserByIdDb(friendId));
            } else {
                throw new ValidationBadRequestException("Пользователь с указанным id не найден: " + friendId);
            }
        } else {
            throw new ValidationBadRequestException("Пользователь с указанным id не найден: " + userId);
        }
        return result;
    }

    public User removeUserByIdDB(int id){
        User user = userDbStorage.getUserByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + id));
        userDbStorage.removeUser(id);
        return user;
    }

    public List<User> deleteFromFriendDb(int userId, int friendId) {
        List<User> result = new ArrayList<>();
        Optional<User> askFriendship = Optional.ofNullable(getUserByIdDb(userId));
        Optional<User> friend = Optional.ofNullable(getUserByIdDb(friendId));
        if(askFriendship.isPresent()) {
            result.add(getUserByIdDb(userId));
            if(friend.isPresent()) {
                result.add(getUserByIdDb(friendId));
                userDbStorage.deleteFriend(userId, friendId);
            } else {
                throw new ValidationBadRequestException("Пользователь с указанным id не найден: " + friendId);
            }
        } else {
            throw new ValidationBadRequestException("Пользователь с указанным id не найден: " + userId);
        }
        return result;
    }

    private void validateUser(User user) {
        String userName = user.getName();
        if (userName == null || userName.isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getUserFriendsDb(int id) {
        return userDbStorage.getAllFriendsById(id);
    }

    public List<User> getCommonFriendsDb(int id, int otherId) {
        List<User> result = new ArrayList<>();
        Optional<User> askFriendship = Optional.ofNullable(getUserByIdDb(id));
        Optional<User> friend = Optional.ofNullable(getUserByIdDb(otherId));
        if(askFriendship.isPresent()) {
            if(friend.isPresent()) {
                result = userDbStorage.getCommonFriends(id, otherId);
            } else {
                throw new ValidationBadRequestException("Пользователь с указанным id не найден: " + otherId);
            }
        } else {
            throw new ValidationBadRequestException("Пользователь с указанным id не найден: " + id);
        }
        return result;
    }

    public User getUserByIdDb(int id) {
        return userDbStorage.getUserByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + id));
    }

    public Boolean isThereEmailDb(User user) {
        String userEmail = user.getEmail();
        int userId = user.getId();
        boolean checkEmail = false;
        for (User userCheck : userDbStorage.getAll()) {
            if (userCheck.getEmail().equals(userEmail) && (userCheck.getId() != userId)) {
                checkEmail = true;
                break;
            }
        }
        return checkEmail;
    }

    private int getUniqueId() {
        return ++uniqueId;
    }

}
