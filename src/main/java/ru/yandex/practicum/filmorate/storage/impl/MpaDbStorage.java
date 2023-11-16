package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<FilmMpaRating> getAllRatingMpa() {
        String mpaSql = "SELECT * FROM FILM_MPA_RATING";
        return jdbcTemplate.query(mpaSql, MpaDbStorage::makeMpa);
    }

    @Override
    public FilmMpaRating getMpaById(int id) {
        String mpaByIdSql = "SELECT * FROM FILM_MPA_RATING WHERE ID = ?";
        List<FilmMpaRating> mpa = jdbcTemplate.query(mpaByIdSql, MpaDbStorage::makeMpa, id);
        if (mpa.size() != 1) {
            throw new EntityNotFoundException(String.format("Рейтингов c id %s более 1", id));
        }
        return mpa.get(0);
    }

    @Override
    public FilmMpaRating getMpaByFilmId(int id) {
        SqlRowSet filmMpaSql = jdbcTemplate.queryForRowSet("select rating_mpa_id from FILMS where id = ?", id);
        if (filmMpaSql.next()) {
            getMpaById(filmMpaSql.getInt("rating_mpa_id"));
        }
        return null;
    }

    static FilmMpaRating makeMpa(ResultSet rs, int rowNum) throws SQLException {
        return FilmMpaRating.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build();
    }

    @Override
    public void addMpa(int filmId, int mpaId) {
        String addMpaSql = "update FILMS set rating_mpa_id = ? where id = ?";
        jdbcTemplate.update(addMpaSql,
                mpaId,
                filmId
        );
    }


//    @Override
//    public List<FilmMpaRating> getAllRatingMpa() {
//        List<FilmMpaRating> allMpa = new ArrayList<>();
//        SqlRowSet mpaSql = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_MPA_RATING");
//        while (mpaSql.next()) {
//            FilmMpaRating mpa = new FilmMpaRating(mpaSql.getInt("id"), mpaSql.getString("name"));
//            allMpa.add(mpa);
//        }
//        return allMpa;
//    }
//
//    @Override
//    public Optional<FilmMpaRating> getMpaById(int id) {
//        SqlRowSet mpaByIdSql = jdbcTemplate.queryForRowSet("SELECT * FROM FILM_MPA_RATING WHERE ID = ?", id);
//        if (mpaByIdSql.next()) {
//            return Optional.of(new FilmMpaRating(mpaByIdSql.getInt("id"), mpaByIdSql.getString("name")));
//        } else {
//            throw new EntityNotFoundException("Список фильмов пуст");
//        }
//    }

}
