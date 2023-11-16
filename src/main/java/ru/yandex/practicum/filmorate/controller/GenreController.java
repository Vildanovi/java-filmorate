package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreDbService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
@Validated
public class GenreController {

    private final GenreDbService service;

    @Operation(summary = "Получить все жанры")
    @GetMapping
    public List<Genre> getAllGenre() {
        log.debug("Получаем все жанры");
        return service.getAll();
    }

    @Operation(summary = "Получение жанра по id")
    @GetMapping("/{id}")
    public Optional<Genre> getGenre(@PathVariable("id") int id) {
        log.debug("Получаем жанр с id: {}", id);
        return service.getGenreByID(id);
    }
}
