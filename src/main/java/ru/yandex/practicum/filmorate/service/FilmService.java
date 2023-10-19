package ru.yandex.practicum.filmorate.service;

//Создайте FilmService, который будет отвечать за операции с фильмами,
// — добавление и удаление лайка, вывод 10 наиболее популярных
// фильмов по количеству лайков. Пусть пока каждый пользователь
// может поставить лайк фильму только один раз.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class FilmService {

    InMemoryFilmStorage inMemoryFilmStorage;
    private int uniqueId = 0;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Film createFilm(Film film) {
        if (film == null) {
            throw new FilmNotFoundException("Необходимо передать параметры фильма");
        }
        film.setId(getUniqueId());
        inMemoryFilmStorage.addFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        int id = film.getId();
        Film updatedFilm = inMemoryFilmStorage.getFilmByID(id);
        if (updatedFilm == null) {
            throw new ValidationException("Фильм с указанным id не найден: " + id);
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
        return inMemoryFilmStorage.getFilmByID(id);
    }

//    public List<User> addLike(Integer filmId, Integer userId) {
//        List<User> result = new ArrayList<>();
//        User firstUser = inMemoryUserStorage.getUserByID(userId);
//        User secondUser = inMemoryUserStorage.getUserByID(friendId);
//        result.add(firstUser);
//        result.add(secondUser);
//        firstUser.getFriends().add(friendId);
//        secondUser.getFriends().add(userId);
//        return result;
//    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
