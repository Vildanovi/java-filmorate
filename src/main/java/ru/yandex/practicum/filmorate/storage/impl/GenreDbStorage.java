package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final Logger log = LoggerFactory.getLogger(GenreDbStorage.class);

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String genreSql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(genreSql, GenreDbStorage::makeGenre);
    }

    @Override
    public Optional<Genre> getGenreByID(int id) {
        String genreByIdSql = "SELECT * FROM GENRE WHERE ID = ?";
        List<Genre> genres = jdbcTemplate.query(genreByIdSql, GenreDbStorage::makeGenre, id);
        if (genres.size() != 1) {
            throw new EntityNotFoundException(String.format("Жанров c id %s более 1", id));
        }
        return Optional.ofNullable(genres.get(0));
    }

    public void addGenre(Film film) {
        Set<Genre> genres = film.getGenres();
        String sqlQuery = "insert into FILM_GENRE(FILM_ID, GENRE_ID) "
                + "VALUES (?, ?)";
        List<Genre> genresTable = new ArrayList<>(genres);
        this.jdbcTemplate.batchUpdate(sqlQuery, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, film.getId());
                ps.setInt(2, genresTable.get(i).getId());
            }

            public int getBatchSize() {
                return genresTable.size();
            }
        });
    }

    @Override
    public Set<Genre> getFilmGenre(int id) {
        String genresFilm = "select * from GENRE JOIN film_genre ON genre_id = GENRE.ID where film_genre.FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(genresFilm, GenreDbStorage::makeGenre, id));
    }

    @Override
    public void deleteGenres(int filmId) {
        String sqlQuery = "delete from film_genre where film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }
}
