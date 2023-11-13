package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationBadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmDbService implements FilmStorage {

    private int uniqueId = 0;
    private final FilmStorage filmDbStorage;
    private final UserStorage userDbStorage;

    @Autowired
    public FilmDbService(@Qualifier("FilmDbStorage") FilmStorage filmStorage, @Qualifier("UserDbStorage") UserStorage userStorage) {
        this.filmDbStorage = filmStorage;
        this.userDbStorage = userStorage;
    }

    @Override
    public Film addFilm(Film film) {
        if (film == null) {
            throw new EntityNotFoundException("Необходимо передать параметры фильма");
        }
        film.setId(getUniqueId());
        filmDbStorage.addFilm(film);
        return film;
    }

    @Override
    public void removeFilm(int id) {
        filmDbStorage.getFilmByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id));
        filmDbStorage.removeFilm(id);
    }

    @Override
    public Film updateFilm(Film film) {
        log.debug("Обновляем фильм {}", film);
        int filmId = film.getId();
        Optional<Film> updatedFilm = filmDbStorage.getFilmByID(filmId);
        if(updatedFilm.isPresent()) {
            filmDbStorage.updateFilm(film);
        } else {
            throw new ValidationBadRequestException("Фильм с указанным id не найден: " + filmId);
        }
        return film;
    }

    @Override
    public List<Film> getAll() {
        return filmDbStorage.getAll();
    }

    @Override
    public Optional<Film> getFilmByID(int id) {
        return Optional.ofNullable(filmDbStorage.getFilmByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id)));
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        filmDbStorage.getFilmByID(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + filmId));
        userDbStorage.getUserByID(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + userId));
        return filmDbStorage.addLike(filmId, userId);
    }

    @Override
    public Optional<Film> deleteLike(int filmId, int userId) {
        filmDbStorage.getFilmByID(filmId)
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + filmId));
        userDbStorage.getUserByID(userId)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не найден: " + userId));
        return filmDbStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopular(int count) {
        log.debug("Получаем {} популярных фильмов", count);
        Optional<List<Film>> popularFilms = Optional.ofNullable(filmDbStorage.getAll());
        if(popularFilms.isPresent()) {
            return filmDbStorage.getPopular(count);
        } else {
            throw new ValidationBadRequestException("Список фильмов пуст");
        }
    }

    public List<Genre> getAllGenre() {
        return filmDbStorage.getAllGenre();
    }

    public Optional<Genre> getGenreByID(int id) {
        return Optional.ofNullable(filmDbStorage
                .getGenreByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Жанр с указанным id не найден: " + id)));
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
