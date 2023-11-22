package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.service.FilmDbService;

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
public class FilmControllerTest extends FilmController {

    HttpClient client;

    @Autowired
    private ObjectMapper objectMapper;
    private Validator validator;

    private FilmDbService filmService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmControllerTest(FilmDbService filmService) {
        super(filmService);
    }

    @BeforeEach
    void beforeEach() {
        client = HttpClient.newHttpClient();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validationFilmEmptyName() {
        Film film = Film.builder()
                .name("")
                .description("Description")
                .releaseDate(LocalDate.of(1900,03,25))
                .mpa(FilmMpaRating.builder()
                        .id(1)
                        .name("mpa")
                        .build())
                .duration(200)
                .build();

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmReleaseDate() {
        Film film = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1894,10,25))
                .mpa(FilmMpaRating.builder()
                        .id(1)
                        .name("mpa")
                        .build())
                .duration(200)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmDescription() {
        Film film = Film.builder()
                .name("Name")
                .description("Пятеро друзей ( комик-группа «Шарло»), приезжают в город Бризуль. " +
                        "Здесь они хотят разыскать господина Огюста Куглова, который задолжал им деньги, " +
                        "а именно 20 миллионов. о Куглов, который за время «своего отсутствия», " +
                        "стал кандидатом Коломбани.")
                .releaseDate(LocalDate.of(1900,3,25))
                .mpa(FilmMpaRating.builder()
                        .id(1)
                        .name("mpa")
                        .build())
                .duration(200)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmDuration() {
        Film film = Film.builder()
                .name("Film name")
                .description("Description")
                .releaseDate(LocalDate.of(1900,3,25))
                .mpa(FilmMpaRating.builder()
                        .id(1)
                        .name("mpa")
                        .build())
                .duration(-200)
                .build();
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void getFilmsMap() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/films"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void getFilms() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/films"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
