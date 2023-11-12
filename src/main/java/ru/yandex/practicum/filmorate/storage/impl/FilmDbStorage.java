package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Optional;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFilm(Film film) {
        String userAddSql = "insert into FILMS(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPA_ID) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(userAddSql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingMpa()
        );
    }

    @Override
    public void removeFilm(int id) {

    }

    @Override
    public void updateFilm(Film film) {

    }

    @Override
    public List<Film> getAll() {
        return null;
    }

    @Override
    public Optional<Film> getFilmByID(int id) {

        return Optional.empty();
    }
}
