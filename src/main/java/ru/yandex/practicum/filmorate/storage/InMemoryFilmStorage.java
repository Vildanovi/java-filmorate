package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int uniqueId = 0;

//    @Autowired
//    public InMemoryFilmStorage() {
//    }

    @Override
    public void addFilm(Film film) {
        film.setId(getUniqueId());
        films.put(film.getId(), film);
    }

    @Override
    public void removeFilm(int id) {
        films.remove(id);
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Optional<Film> getFilmByID(int id) {
        return Optional.ofNullable(films.get(id));
    }

    private int getUniqueId() {
        return ++uniqueId;
    }
}
