package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserControllerTest extends UserController {

    UserController userController;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
    }

    @Test
    public void createUser() throws ValidationException {
        User user1 = new User();
        user1.setLogin("dolore");
        user1.setName("Nick Name");
        user1.setEmail("mail@mail.ru");
        user1.setBirthday(LocalDate.of(1946,8,20));
        User newUser = userController.postUser(user1);
        assertEquals(userController.users.get(newUser.getId()), newUser);
    }

    @Test
    public void updateUser() throws ValidationException {
        User user1 = new User();
        user1.setLogin("dolore");
        user1.setName("Nick Name");
        user1.setEmail("mail@mail.ru");
        user1.setBirthday(LocalDate.of(1946,8,20));
        User newUser = userController.postUser(user1);

        User user2 = new User();
        user2.setLogin("doloreUpdate");
        user2.setName("est adipisicing");
        user2.setId(1);
        user2.setEmail("mail@yandex.ru");
        user2.setBirthday(LocalDate.of(1976,9,20));
        User updateUser = userController.putUser(user2);
        assertEquals(userController.users.get(newUser.getId()), updateUser);
    }
}
