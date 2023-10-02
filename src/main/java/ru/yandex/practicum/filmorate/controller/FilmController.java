package ru.yandex.practicum.filmorate.controller;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    protected int uniqueId = 0;

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    public Map<Integer, Film> films = new HashMap<>();

    @GetMapping()
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) throws ValidationException {
        log.debug("Сохраняем фильм {}", film);
        film.setId(getUniqueId());
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film putFilm(@RequestBody Film film) throws ValidationException {
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
        films.put(id, updatedFilm);
        log.debug("Обновляем фильм {}", film);
        return updatedFilm;
    }

    private void validateFilm(Film film) throws ValidationException {
        LocalDate minDate = LocalDate.of(1895, 12, 28);
        LocalDate releaseDate = film.getReleaseDate();
        int durationMinutes = film.getDuration();
//        int descriptionLength = film.getDescription().length();
        LocalDate currentDate = LocalDate.now();
        if (releaseDate.isAfter(currentDate)) {
            throw new ValidationException("Дата релиза позже текущей: " + releaseDate);
        }
//        if (descriptionLength > 200) {
//            throw new ValidationException("Описание более 200 символов: " + descriptionLength);
//        }
        if (releaseDate.isBefore(minDate)) {
            throw new ValidationException("Дата ранее 28 декабря 1895 года: " + releaseDate);
        }
        if (durationMinutes < 0) {
            throw new ValidationException("Продолжительность меньше 0: " + durationMinutes);
        }
    }

    public int getUniqueId() {
        return ++uniqueId;
    }
}
