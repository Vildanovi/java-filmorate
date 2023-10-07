package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private int uniqueId = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        validateUser(user);
        int id = getUniqueId();
        user.setId(id);
        users.put(user.getId(), user);
        log.debug("Создаем пользователя {}", user);
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        log.debug("Обновляем пользователя {}", user);
        validateUser(user);
        int userId = user.getId();
        if (!users.containsKey(userId)) {
            throw new ValidationException("Пользователь с указанным login не найден: " + userId);
        }
        User updatedUser = users.get(userId);
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

    private int getUniqueId() {
        return ++uniqueId;
    }
}
