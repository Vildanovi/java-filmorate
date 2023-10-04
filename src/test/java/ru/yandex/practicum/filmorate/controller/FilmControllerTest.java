package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Set;

import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.Validation;
import javax.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FilmControllerTest extends FilmController {

    FilmController filmController;
    HttpClient client;
    @Autowired
    private ObjectMapper objectMapper;
    private Validator validator;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
        client = HttpClient.newHttpClient();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void validationFilmEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1900,03,25));
        film.setDuration(200);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmDescription() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Пятеро друзей ( комик-группа «Шарло»), " +
                "приезжают в город Бризуль. Здесь они хотят разыскать господина " +
                "Огюста Куглова, который задолжал им деньги, а именно 20 миллионов. " +
                "о Куглов, который за время «своего отсутствия», стал кандидатом Коломбани.");
        film.setReleaseDate(LocalDate.of(1900,3,25));
        film.setDuration(200);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
    }

    @Test
    public void validationFilmDuration() {
        Film film = new Film();
        film.setName("Film name");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(1900,3,25));
        film.setDuration(-200);
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
    public void createFilm() throws ValidationException {
        Film film1 = new Film();
        film1.setName("nisi eiusmod");
        film1.setDescription("adipisicing");
        film1.setReleaseDate(LocalDate.of(1967,3,25));
        film1.setDuration(100);
        Film newFilm = filmController.postFilm(film1);
        assertEquals(filmController.getFilms().get(newFilm.getId()), newFilm);
    }

    @Test
    public void updateFilm() throws ValidationException {
        Film film1 = new Film();
        film1.setName("nisi eiusmod");
        film1.setDescription("adipisicing");
        film1.setReleaseDate(LocalDate.of(1967,3,25));
        film1.setDuration(100);
        Film newFilm = filmController.postFilm(film1);

        Film film2 = new Film();
        film2.setId(1);
        film2.setName("Film Updated");
        film2.setDescription("New film update decription");
        film2.setReleaseDate(LocalDate.of(1989,4,17));
        film2.setDuration(190);
        Film updateFilm = filmController.putFilm(film2);
        assertEquals(filmController.getFilms().get(newFilm.getId()), updateFilm);
    }

    @Test
    public void createTrueFilm() throws IOException, InterruptedException {
        Film film1 = new Film();
        film1.setName("nisi eiusmod");
        film1.setDescription("adipisicing");
        film1.setReleaseDate(LocalDate.of(1967,03,25));
        film1.setDuration(100);
        String json = objectMapper.writeValueAsString(film1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/films"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}
