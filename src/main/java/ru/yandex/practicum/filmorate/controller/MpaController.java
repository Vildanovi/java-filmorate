package ru.yandex.practicum.filmorate.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.service.MpaDbService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Validated
public class MpaController {

    private final MpaDbService service;

    @Operation(summary = "Получить все mpa рейтинги")
    @GetMapping
    public List<FilmMpaRating> getAllRatingMpa() {
        log.debug("Получаем все рейтинги mpa");
        return service.getAllRatingMpa();
    }

    @Operation(summary = "Получение рейтинг mpa по id")
    @GetMapping("/{id}")
    public FilmMpaRating getMpaById(@PathVariable("id") int id) {
        log.debug("Получаем рейтинг с id: {}", id);
        return service.getMpaById(id);
    }
}