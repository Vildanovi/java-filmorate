package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserDbService userService;

    @Operation(summary = "Получить всех пользователей")
    @GetMapping()
    public List<User> getAllUsers() {
        return new ArrayList<>(userService.getAllUsersDb());
    }

    @Operation(summary = "Добавить пользователя")
    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        log.debug("Создаем пользователя {}", user);
        return userService.createUserDb(user);
    }

    @Operation(summary = "Получение пользователя по id")
    @GetMapping("/{id}")
    public User getUser(@PathVariable ("id") int id) {
        log.debug("Получаем пользователя с id: {}", id);
        return userService.getUserByIdDb(id);
    }

    @Operation(summary = "Обновить параметры пользователя")
    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        return userService.putUserDb(user);
    }

    @Operation(summary = "Возвращаем список пользователей, являющихся его друзьями")
    @GetMapping("/{id}/friends")
    public List<User> getUserFriendsById(@PathVariable ("id") int id) {
        return userService.getUserFriendsDb(id);
    }

    @Operation(summary = "Добавление в друзья")
    @PutMapping("/{id}/friends/{friendId}")
    public List<User> addFriends(@PathVariable ("id") int id, @PathVariable ("friendId") int friendId) {
        log.debug("Добавляем в друзья. Мой id = {}, друг friendId = {}", id, friendId);
        return userService.addToFriendDb(id, friendId);
    }

    @Operation(summary = "Удаление из друзей")
    @DeleteMapping("/{id}/friends/{friendId}")
    public List<User> deleteFriends(@PathVariable ("id") int id, @PathVariable ("friendId") int friendId) {
        log.debug("Удаляем из друзей. Мой id = {}, друг friendId = {}", id, friendId);
        return userService.deleteFromFriendDb(id, friendId);
    }

    @Operation(summary = "Удаление пользователя")
    @DeleteMapping("/{id}")
    public User deleteFriends(@PathVariable ("id") int id) {
        log.debug("Удаляем пользователя с id = {}", id);
        return userService.removeUserByIdDB(id);
    }

    @Operation(summary = "Cписок друзей, общих с другим пользователем.")
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable ("id") int id, @PathVariable ("otherId") int  otherId) {
        log.debug("Ищем общих друзей. Мой id = {}, пользователь otherId = {}", id, otherId);
        return userService.getCommonFriendsDb(id, otherId);
    }
}
