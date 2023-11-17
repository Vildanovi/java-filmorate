package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.ValidationBadRequestException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserDbService;
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

    HttpClient client;
    @Autowired
    private ObjectMapper objectMapper;
    private Validator validator;

    UserDbService userService;

    @Autowired
    JdbcTemplate jdbcTemplate;


    @Autowired
    public UserControllerTest(UserDbService userService) {
        super(userService);
    }

    @BeforeEach
    void beforeEach() {
        client = HttpClient.newHttpClient();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        cleanUsers();
    }

    public void cleanUsers() {
        String query = "DELETE FROM USERS";
        jdbcTemplate.update(query);
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
        User user = User.builder()
                .login("Login")
                .name("")
                .email("mail.ru")
                .birthday(LocalDate.of(1980,8,20))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserLoginEmpty() {
        User user = User.builder()
                .login("dolore ullamco")
                .name("")
                .email("yandex@mail.ru")
                .birthday(LocalDate.of(1980,8,20))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserEmailEmpty() {
        User user = User.builder()
                .login("login")
                .name("Name")
                .email("")
                .birthday(LocalDate.of(1980,8,20))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserEmailWhiteSpace() {
        User user = User.builder()
                .login("dolore ullamco")
                .name("dolore")
                .email("yandex@mail.ru")
                .birthday(LocalDate.of(1980,8,20))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationUserBirthday() {
        User user = User.builder()
                .login("Login")
                .name("Name")
                .email("yandex@mail.ru")
                .birthday(LocalDate.of(2025,10,5))
                .build();
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(1, violations.size());
    }

    @Test
    public void createTrueUser() throws IOException, InterruptedException {
        User user1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail@mail.ru")
                .birthday(LocalDate.of(1946,8,20))
                .build();
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
        User user1 = User.builder()
                .login("dolore")
                .name("Nick Name")
                .email("mail2@mail.ru")
                .birthday(LocalDate.of(1946,8,20))
                .build();

        String json = objectMapper.writeValueAsString(user1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        User user2 = User.builder()
                .id(2)
                .login("dolore2")
                .name("est adipisicing")
                .email("mail@ya.ru")
                .birthday(LocalDate.of(1976,9,20))
                .build();
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
