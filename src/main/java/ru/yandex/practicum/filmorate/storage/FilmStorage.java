package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import java.util.List;

public interface FilmStorage {

    void addFilm(Film film);

    void removeFilm(int id);

    void updateFilm(Film film);

    List<Film> getAll();

    Film getFilmByID(int id);
}