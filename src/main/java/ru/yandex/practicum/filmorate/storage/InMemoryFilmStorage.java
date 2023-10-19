package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
//    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public void addFilm(Film film) {
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
    public Film getFilmByID(int id) {
        return films.get(id);
    }
}
