package ru.yandex.practicum.filmorate.controller;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private int uniqueId = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping()
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        log.debug("Сохраняем фильм {}", film);
        film.setId(getUniqueId());
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            throw new ValidationException("Фильм с указанным id не найден: " + id);
        }
        validateFilm(film);
        Film updatedFilm = films.get(id);
        updatedFilm.setName(film.getName());
        updatedFilm.setDescription(film.getDescription());
        updatedFilm.setReleaseDate(film.getReleaseDate());
        updatedFilm.setDuration(film.getDuration());
        log.debug("Обновляем фильм {}", film);
        return updatedFilm;
    }

    private void validateFilm(Film film) {
        LocalDate releaseDate = film.getReleaseDate();
        LocalDate currentDate = LocalDate.now();
        if (releaseDate.isAfter(currentDate)) {
            throw new ValidationException("Дата релиза позже текущей: " + releaseDate);
        }
        if (releaseDate.isBefore(MIN_DATE)) {
            throw new ValidationException("Дата ранее 28 декабря 1895 года: " + releaseDate);
        }
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
