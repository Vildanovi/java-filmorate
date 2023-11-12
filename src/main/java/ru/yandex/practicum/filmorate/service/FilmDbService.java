package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
    public void addFilm(Film film) {
        if (film == null) {
            throw new EntityNotFoundException("Необходимо передать параметры фильма");
        }
        film.setId(getUniqueId());
        filmDbStorage.addFilm(film);
    }

    @Override
    public void removeFilm(int id) {

    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public List<Film> getAll() {
        return filmDbStorage.getAll();
    }

    @Override
    public Optional<Film> getFilmByID(int id) {
        return Optional.empty();
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
