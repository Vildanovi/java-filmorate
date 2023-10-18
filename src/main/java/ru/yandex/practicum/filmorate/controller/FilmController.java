package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
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
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    private int uniqueId = 0;
    private final Map<Integer, Film> films = new HashMap<>();
//    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

//    @Autowired
//    public FilmController(FilmService filmService) {
//        this.filmService = filmService;
//    }

    @Operation(summary = "Получить все фильмы")
    @GetMapping()
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Operation(summary = "Добавление фильма")
    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        log.debug("Сохраняем фильм {}", film);
        return filmService.createFilm(film);
    }

    @Operation(summary = "Обновление фильма")
    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        int id = film.getId();
        if (!films.containsKey(id)) {
            throw new ValidationException("Фильм с указанным id не найден: " + id);
        }

        Film updatedFilm = films.get(id);
        updatedFilm.setName(film.getName());
        updatedFilm.setDescription(film.getDescription());
        updatedFilm.setReleaseDate(film.getReleaseDate());
        updatedFilm.setDuration(film.getDuration());
        log.debug("Обновляем фильм {}", film);
        return updatedFilm;
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
