package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Deprecated
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int uniqueId = 0;

    @Override
    public Film addFilm(Film film) {
        film.setId(getUniqueId());
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void removeFilm(int id) {
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmByID(int id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Film> deleteLike(int filmId, int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Film> getPopular(int count) {
        throw new UnsupportedOperationException();
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
