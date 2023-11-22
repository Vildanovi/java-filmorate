package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Service
public class GenreDbService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreDbService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }

    public Genre getGenreByID(int id) {
        return genreStorage.getGenreByID(id)
                .orElseThrow(() -> new EntityNotFoundException("Жанр с указанным id не найден: " + id));
    }
}
