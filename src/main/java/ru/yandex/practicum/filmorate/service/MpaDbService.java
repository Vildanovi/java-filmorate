package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaDbService {

    private final MpaStorage mpaStorage;

    @Autowired
    public MpaDbService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<FilmMpaRating> getAllRatingMpa() {
        return mpaStorage.getAllRatingMpa();
    }

    public FilmMpaRating getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }
}
