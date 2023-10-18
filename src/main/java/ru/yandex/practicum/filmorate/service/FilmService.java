package ru.yandex.practicum.filmorate.service;

//Создайте FilmService, который будет отвечать за операции с фильмами,
// — добавление и удаление лайка, вывод 10 наиболее популярных
// фильмов по количеству лайков. Пусть пока каждый пользователь
// может поставить лайк фильму только один раз.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {

    InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film createFilm(Film film) {
        if (film == null) {
            throw new FilmNotFoundException("Необходимо передать параметры фильма");
        }
        inMemoryFilmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        Film existFilm = inMemoryFilmStorage.getFilmByID(film.getId());
        if (existFilm == null) {
            throw new FilmNotFoundException("Необходимо передать параметры фильма");
        }
        inMemoryFilmStorage.updateFilm(film);
        return film;
    }

    public List<Film> getFilms() {
        return new ArrayList<>(inMemoryFilmStorage.getAll());
    }

}
