package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    Film addFilm(Film film);

    void removeFilm(int id);

    Film updateFilm(Film film);

    List<Film> getAll();

    Optional<Film> getFilmByID(int id);

    Optional<Film> addLike(int filmId, int userId);

    Optional<Film> deleteLike(int filmId, int userId);

    List<Film> getPopular(int count);

    List<Genre> getAllGenre();

    Optional<Genre> getGenreByID(int id);
}