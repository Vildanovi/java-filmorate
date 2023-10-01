package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTest extends FilmController {

    FilmController filmController;

    @BeforeEach
    void beforeEach() {
        filmController = new FilmController();
    }

    @Test
    public void createFilm() throws ValidationException {
        Film film1 = new Film();
        film1.setName("nisi eiusmod");
        film1.setDescription("adipisicing");
        film1.setReleaseDate(LocalDate.of(1967,3,25));
        film1.setDuration(100);
        Film newFilm = filmController.postFilm(film1);
        assertEquals(filmController.films.get(newFilm.getId()), newFilm);
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
        assertEquals(filmController.films.get(newFilm.getId()), updateFilm);
    }
}
