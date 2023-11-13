package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationBadRequestException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(UserDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        String userAddSql = "insert into FILMS(ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_MPA_ID) " +
                "values (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(userAddSql,
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingMpa()
        );
        return film;
    }

    @Override
    public void removeFilm(int id) {
        String sqlQuery = "delete from FILMS where id = ?";
        jdbcTemplate.update(sqlQuery, id);
    }

    @Override
    public Film updateFilm(Film film) {
        String userUpdateSql = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_MPA_ID = ? where id = ?";
        jdbcTemplate.update(userUpdateSql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getRatingMpa(),
                film.getId()
        );
        return film;
    }

    @Override
    public List<Film> getAll() {
        String query = "SELECT * FROM films";
        return jdbcTemplate.query(query, this::makeFilm);
    }

    @Override
    public Optional<Film> getFilmByID(int id) {
        String sqlQuery = "select * from FILMS where id = ?";
        Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        if(film != null) {
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> addLike(int filmId, int userId) {
        String filmLikeSql = "insert into user_film_likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(filmLikeSql,
                filmId,
                userId
        );
        return getFilmByID(filmId);
    }

    @Override
    public Optional<Film> deleteLike(int filmId, int userId) {
        String deleteLikeSql = "delete from user_film_likes where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(deleteLikeSql,
                filmId,
                userId
        );
        return getFilmByID(filmId);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));
        film.setRatingMpa(resultSet.getInt("ratingMpa"));
        film.setFilmGenre(getFilmGenre(film.getId()));
        return film;
    }

    private Set<Integer> getFilmGenre(int id) {
        SqlRowSet filmGenreSql = jdbcTemplate.queryForRowSet("select genre_id from film_genre where film_id = ?", id);
        Set<Integer> filmGenre = new HashSet<>();
        while (filmGenreSql.next()) {
            filmGenre.add(filmGenreSql.getInt("genre_id"));
        }
        return filmGenre;
    }

    public List<Film> getPopular(int count) {
        SqlRowSet filmPopularSql = jdbcTemplate.queryForRowSet("SELECT film_id, " +
                "COUNT(user_id) " +
                "FROM user_film_likes " +
                "GROUP BY film_id " +
                "ORDER BY COUNT(user_id) DESC " +
                "LIMIT ?;", count);
        List<Film> filmPopular = new ArrayList<>();
        while (filmPopularSql.next()) {
            int filmId = filmPopularSql.getInt("film_id");
            filmPopular.add(getFilmByID(filmId)
                    .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + filmId)));
        }
        return filmPopular;
    }

    @Override
    public List<Genre> getAllGenre() {
        List<Genre> allGenre = new ArrayList<>();
        SqlRowSet genreSql = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE");
        while (genreSql.next()) {
            Genre genre = new Genre(genreSql.getInt("id"), genreSql.getString("name"));
            allGenre.add(genre);
        }
        return allGenre;
    }

    @Override
    public Optional<Genre> getGenreByID(int id) {
        SqlRowSet genreByIdSql = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE WHERE ID = ?", id);
        if (genreByIdSql.next()) {
            return Optional.of(new Genre(genreByIdSql.getInt("id"), genreByIdSql.getString("name")));
        } else {
            throw new ValidationBadRequestException("Список фильмов пуст");
        }
    }
}