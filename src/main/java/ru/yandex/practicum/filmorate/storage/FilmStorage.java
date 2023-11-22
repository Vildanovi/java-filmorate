package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    void removeFilm(int id);

    Film updateFilm(Film film);

    List<Film> getAll();

    void load(List<Film> films);

    Optional<Film> getFilmByID(int id);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    List<Film> getPopular(int count);
}