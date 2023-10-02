package ru.yandex.practicum.filmorate.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    protected int uniqueId = 0;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    public final Map<Integer, User> users = new HashMap<>();

    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) throws ValidationException {
        int id = getUniqueId();
        user.setId(id);
        validateUser(user);
        String userName = user.getName();
        if (userName == null) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.debug("Создаем пользователя {}", user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) throws ValidationException {
        log.debug("Обновляем пользователя {}", user);
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new ValidationException("Пользователь с указанным login не найден: " + userId);
        }
        validateUser(user);
        User updatedUser = users.get(userId);
        updatedUser.setName(user.getEmail());
        updatedUser.setLogin(user.getLogin());
        updatedUser.setName(user.getName());
        updatedUser.setBirthday(user.getBirthday());
        users.put(userId, updatedUser);
        return updatedUser;
    }

    private void validateUser(User user) throws ValidationException {
        String userLogin = user.getLogin();
        LocalDate birthdayDate = user.getBirthday();
        LocalDate currentDate = LocalDate.now();
        if (userLogin.contains(" ")) {
            throw new ValidationException("В логине есть пробелы: " + userLogin);
        }
        if (birthdayDate.isAfter(currentDate)) {
            throw new ValidationException("Дата рождения позже текущей: " + birthdayDate);
        }
    }

    public int getUniqueId() {
        return ++uniqueId;
    }
}
