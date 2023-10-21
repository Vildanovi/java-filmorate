package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage inMemoryFilmStorage;
    private final UserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(FilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film createFilm(Film film) {
        if (film == null) {
            throw new EntityNotFoundException("Необходимо передать параметры фильма");
        }
        inMemoryFilmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        int id = film.getId();
        Film updatedFilm = inMemoryFilmStorage.getFilmByID(id);
        if (updatedFilm == null) {
            throw new EntityNotFoundException("Фильм с указанным id не найден: " + id);
        }
        updatedFilm.setName(film.getName());
        updatedFilm.setDescription(film.getDescription());
        updatedFilm.setReleaseDate(film.getReleaseDate());
        updatedFilm.setDuration(film.getDuration());
        return film;
    }

    public List<Film> getFilms() {
        return new ArrayList<>(inMemoryFilmStorage.getAll());
    }

    public void deleteFilm(int id) {
        inMemoryFilmStorage.removeFilm(id);
    }

    public Film getFilmById(int id) {
        Film result = inMemoryFilmStorage.getFilmByID(id);
        if (result == null) {
            throw new EntityNotFoundException("Фильм с указанным id не найден: " + id);
        }
        return inMemoryFilmStorage.getFilmByID(id);
    }

    public Film addLike(Integer filmId, Integer userId) {
        Film likedFilm = inMemoryFilmStorage.getFilmByID(filmId);
        likedFilm.getLikes().add(userId);
        return likedFilm;
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        Film result = inMemoryFilmStorage.getFilmByID(filmId);
        User user = inMemoryUserStorage.getUserByID(userId).orElseThrow(() -> new EntityNotFoundException("Пользователь с указанным id не существует: " + userId));
        if (result == null) {
            throw new EntityNotFoundException("Фильм с указанным id не найден: " + filmId);
        }
        result.getLikes().remove(user.getId());
        return result;
    }

    public List<Film> getPopular(int count) {
        return inMemoryFilmStorage.getAll().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
