package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import java.util.List;

public interface MpaStorage {

    List<FilmMpaRating> getAllRatingMpa();

    FilmMpaRating getMpaById(int id);

    FilmMpaRating getMpaByFilmId(int id);

    void addMpa(int filmId, int mpaId);
}
