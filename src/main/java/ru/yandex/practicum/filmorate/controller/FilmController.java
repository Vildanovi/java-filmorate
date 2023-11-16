package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmDbService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
@Validated
public class FilmController {

    private final FilmDbService filmService;

    @Operation(summary = "Получить все фильмы")
    @GetMapping
    public List<Film> getAllFilms() {

        return filmService.getAll();
    }

    @Operation(summary = "Получение фильма по id")
    @GetMapping("/{id}")
    public Film getFilm(@PathVariable ("id") int id) {
        log.debug("Получаем фильм с id: {}", id);
        return filmService.getFilmByID(id);
    }

    @Operation(summary = "Добавление фильма")
    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        log.debug("Сохраняем фильм {}", film);
        return filmService.addFilm(film);
    }

    @Operation(summary = "Обновление фильма")
    @PutMapping
    public Film putFilm(@RequestBody Film film) {
        log.debug("Обновляем фильм {}", film);
        return filmService.updateFilm(film);
    }

    @Operation(summary = "Пользователь ставит лайк фильму.")
    @PutMapping("/{id}/like/{userId}")
    public Optional<Film> addLike(@PathVariable ("id") int id, @PathVariable ("userId") int userId) {
        log.debug("Ставим лайк фильму с id: {}", id);
        return filmService.addLike(id, userId);
    }

    @Operation(summary = "Пользователь удаляет лайк.")
    @DeleteMapping("/{id}/like/{userId}")
    public Optional<Film> deleteLike(@PathVariable ("id") int id, @PathVariable ("userId") int userId) {
        log.debug("Удаляем лайк фильма с id: {}", id);
        return filmService.deleteLike(id, userId);
    }

    @Operation(summary = "Возвращает список из первых count фильмов по количеству лайков. " +
            "Если значение параметра count не задано, верните первые 10.")
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10") @Positive Integer count) {
        log.debug("Возвращаем популярные фильмы в количестве: {}", count);
        return filmService.getPopular(count);
    }
}
