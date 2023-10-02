package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest extends UserController {

    UserController userController;
    HttpClient client;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void beforeEach() {
        userController = new UserController();
        client = HttpClient.newHttpClient();
    }

    @Test
    public void getUsers() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void createTrueUser() throws IOException, InterruptedException {
        User user1 = new User();
        user1.setLogin("dolore");
        user1.setName("Nick Name");
        user1.setEmail("mail@mail.ru");
        user1.setBirthday(LocalDate.of(1946,8,20));
        String json = objectMapper.writeValueAsString(user1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
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
