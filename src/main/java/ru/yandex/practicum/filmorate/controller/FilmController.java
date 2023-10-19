package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import javax.validation.Valid;
import java.util.List;

@Data
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @Operation(summary = "Получить все фильмы")
    @GetMapping()
    public List<Film> getAllFilms() {
        return filmService.getFilms();
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
        log.debug("Обновляем фильм {}", film);
        return filmService.updateFilm(film);
    }

    @Operation(summary = "Пользователь ставит лайк фильму.")
    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable ("id") int id, @PathVariable ("userId") int userId) {
        log.debug("Ставим лайк фильму с id: {}", id);
        return filmService.addLike(id, userId);
    }

    @Operation(summary = "Возвращает список из первых count фильмов по количеству лайков. " +
            "Если значение параметра count не задано, верните первые 10.")
    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(value = "count", defaultValue = "10", required = false) Integer count) {
        log.debug("Возвращаем популярные фильмы в количестве: {}", count);
        return filmService.getPopular(count);
    }
}
