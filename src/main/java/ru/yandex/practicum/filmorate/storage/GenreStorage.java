package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    List<Genre> getAll();

    Optional<Genre> getGenreByID(int id);

    void addGenre(Film film);

    Set<Genre> getFilmGenre(int id);

    void deleteGenres(int filmId);
}
