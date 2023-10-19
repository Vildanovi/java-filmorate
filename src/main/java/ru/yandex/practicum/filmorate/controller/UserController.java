package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Получить всех пользователей")
    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(userService.getAllUsers());
    }

    @Operation(summary = "Добавить пользователя")
    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.debug("Создаем пользователя {}", user);
        return userService.createUser(user);
    }

    @Operation(summary = "Обновить параметры пользователя")
    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        return userService.putUser(user);
    }

    @Operation(summary = "Получить список друзей по id пользователя")
    @GetMapping("/users/{id}/friends")
    public List<User> getUserFriendsById(@PathVariable ("id") int id) {
        return userService.getUserFriends(id);
    }

    @Operation(summary = "Добавить пользователей в друзья")
    @PutMapping("/users/{id}/friends/{friendId}")
    public List<User> addFriends(@PathVariable ("id") int id, @PathVariable ("friendId") int friendId) {
        return userService.addToFriend(id, friendId);
    }

    @Operation(summary = "Удаление друзей")
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public List<User> deleteFriends(@PathVariable ("id") int id, @PathVariable ("friendId") int friendId) {
        return userService.deleteFromFriend(id, friendId);
    }
}
