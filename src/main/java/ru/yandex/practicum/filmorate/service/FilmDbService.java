package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationBadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class FilmDbService {

    private final FilmStorage filmDbStorage;
    private final UserStorage userDbStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Autowired
    public FilmDbService(FilmStorage filmStorage,
                         UserStorage userStorage,
                         GenreStorage genreStorage,
                         MpaStorage mpaStorage) {
        this.filmDbStorage = filmStorage;
        this.userDbStorage = userStorage;
        this.genreStorage = genreStorage;
        this.mpaStorage = mpaStorage;
    }

    public Film addFilm(Film film) {
        if (film == null) {
            throw new EntityNotFoundException("Необходимо передать параметры фильма");
        }
        Film newFilm = filmDbStorage.addFilm(film);
        if (film.getGenres() != null) {
            genreStorage.addGenre(newFilm);
        }
        return newFilm;
    }

    public void removeFilm(int id) {
        filmDbStorage.getFilmByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id));
        filmDbStorage.removeFilm(id);
    }

    public Film updateFilm(Film film) {
        log.debug("Обновляем фильм {}", film);
        int filmId = film.getId();
        Optional<Film> updatedFilm = filmDbStorage.getFilmByID(filmId);
        if (updatedFilm.isPresent()) {
            genreStorage.deleteGenres(filmId);
            filmDbStorage.updateFilm(film);
            if (film.getGenres() != null) {
                genreStorage.addGenre(film);
            }
        } else {
            throw new EntityNotFoundException("Фильм с указанным id не найден: " + filmId);
        }
        return film;
    }

    public List<Film> getAll() {
        List<Film> allFilm = filmDbStorage.getAll();
        filmDbStorage.load(allFilm);
        return allFilm;
    }

    public Film getFilmByID(int id) {
        Film film = filmDbStorage.getFilmByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id));
        FilmMpaRating mpa = mpaStorage.getMpaByFilmId(id);
        Set<Genre> genres = genreStorage.getFilmGenre(id);
        if (mpa != null) {
            film.setMpa(mpa);
        }
        if (genres != null) {
            film.setGenres(genres);
        }
        return film;
    }

    public Film addLike(int filmId, int userId) {
        filmDbStorage.getFilmByID(filmId);
        userDbStorage.getUserByID(userId);
        return filmDbStorage.addLike(filmId, userId);
    }

    public Film deleteLike(int filmId, int userId) {
        filmDbStorage.getFilmByID(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + filmId));
        userDbStorage.getUserByID(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + userId));
        return filmDbStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopular(int count) {
        log.debug("Получаем {} популярных фильмов", count);
        List<Film> popularFilms = filmDbStorage.getAll();
        if (popularFilms != null) {
            return filmDbStorage.getPopular(count);
        } else {
            throw new ValidationBadRequestException("Список фильмов пуст");
        }
    }
}
