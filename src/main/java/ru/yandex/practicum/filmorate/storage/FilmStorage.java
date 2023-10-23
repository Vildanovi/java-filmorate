package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    void addFilm(Film film);

    void removeFilm(int id);

    void updateFilm(Film film);

    List<Film> getAll();

    Optional<Film> getFilmByID(int id);
}