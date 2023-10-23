package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationBadRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserControllerTest extends UserController {

    UserController userController;
    HttpClient client;
    @Autowired
    private ObjectMapper objectMapper;
    private Validator validator;
    private UserService userService;

    @Autowired
    public UserControllerTest(UserService userService) {
        super(userService);
    }

    @BeforeEach
    void beforeEach() {
        userController = new UserControllerTest(userService);
        client = HttpClient.newHttpClient();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void getUsersMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void validationUserEmailUncorrected() {
        User user = new User();
        user.setLogin("Login");
        user.setName("");
        user.setEmail("mail.ru");
        user.setBirthday(LocalDate.of(1980,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserLoginEmpty() {
        User user = new User();
        user.setLogin("dolore ullamco");
        user.setLogin("");
        user.setEmail("yandex@mail.ru");
        user.setBirthday(LocalDate.of(1980,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserEmailEmpty() {
        User user = new User();
        user.setLogin("login");
        user.setName("Name");
        user.setEmail("");
        user.setBirthday(LocalDate.of(1980,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserEmailWhiteSpace() {
        User user = new User();
        user.setLogin("dolore ullamco");
        user.setName("dolore");
        user.setEmail("yandex@mail.ru");
        user.setBirthday(LocalDate.of(1980,8,20));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserBirthday() {
        User user = new User();
        user.setLogin("Login");
        user.setName("Name");
        user.setEmail("yandex@mail.ru");
        user.setBirthday(LocalDate.of(2025,10,5));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
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
    public void updateUser() throws ValidationBadRequestException, IOException, InterruptedException {
        User user1 = new User();
        user1.setLogin("dolore");
        user1.setName("Nick Name");
        user1.setEmail("mail2@mail.ru");
        user1.setBirthday(LocalDate.of(1946,8,20));

        String json = objectMapper.writeValueAsString(user1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        User user2 = new User();
        user2.setId(1);
        user2.setLogin("dolore2");
        user2.setName("est adipisicing");
        user2.setEmail("mail@ya.ru");
        user2.setBirthday(LocalDate.of(1976,9,20));

        String json2 = objectMapper.writeValueAsString(user2);
        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json2))
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response2.statusCode());
    }
}
