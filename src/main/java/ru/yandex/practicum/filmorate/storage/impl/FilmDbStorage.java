package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film addFilm(Film film) {
        Map<String, Object> keys = new SimpleJdbcInsert(this.jdbcTemplate)
                .withTableName("films")
                .usingColumns("NAME", "DESCRIPTION", "RELEASE_DATE", "DURATION", "RATING_MPA_ID")
                .usingGeneratedKeyColumns("ID")
                .executeAndReturnKeyHolder(Map.of("NAME", film.getName(),
                        "DESCRIPTION", film.getDescription(),
                        "RELEASE_DATE", java.sql.Date.valueOf(film.getReleaseDate()),
                        "DURATION", film.getDuration(),
                        "RATING_MPA_ID", film.getMpa().getId()))
                .getKeys();
        film.setId((Integer) keys.get("ID"));
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
                film.getMpa().getId(),
                film.getId()
        );
        return getFilmByID(film.getId())
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + film));
    }

    @Override
    public List<Film> getAll() {
        String query = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_mpa_id, fmr.name as mpa_name " +
                "FROM films f " +
                "LEFT JOIN film_mpa_rating fmr " +
                "ON f.rating_mpa_id = fmr.id";
        return jdbcTemplate.query(query, this::makeFilm);
    }

    @Override
    public Optional<Film> getFilmByID(int id) {
        String sqlQuery = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_mpa_id, fmr.name as mpa_name " +
                "FROM films f " +
                "LEFT JOIN film_mpa_rating fmr " +
                "ON f.rating_mpa_id = fmr.id " +
                "WHERE f.ID = ?";
        return Optional.ofNullable(jdbcTemplate.query(sqlQuery, this::makeFilm, id).stream().findAny()
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id)));
    }

    @Override
    public Film addLike(int filmId, int userId) {
        String filmLikeSql = "insert into user_film_likes(film_id, user_id) " +
                "values (?, ?)";
        jdbcTemplate.update(filmLikeSql,
                filmId,
                userId
        );
        return getFilmByID(filmId).orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + filmId));
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        String deleteLikeSql = "delete from user_film_likes where FILM_ID = ? and USER_ID = ?";
        jdbcTemplate.update(deleteLikeSql,
                filmId,
                userId
        );
        return getFilmByID(filmId).orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + filmId));
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(getLikesById(rs.getInt("id")))
                .mpa(getMpaRating(rs.getInt("rating_mpa_id"), rs.getString("mpa_name")))
                .genres(new HashSet<>())
                .build();
    }

    public Set<Genre> getFilmGenre(int id) {
        String genresFilm = "select * from GENRE JOIN film_genre ON genre_id = GENRE.ID where film_genre.FILM_ID = ?";
        return new HashSet<>(jdbcTemplate.query(genresFilm, GenreDbStorage::makeGenre, id));
    }

    private FilmMpaRating getMpaRating(int mpaId, String mpaName) {
        return  FilmMpaRating.builder()
                .id(mpaId)
                .name(mpaName)
                .build();
    }

    public List<Film> getPopular(int count) {
        String topFilms = "SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rating_mpa_id, fmr.name as mpa_name " +
                "FROM films as f " +
                "LEFT JOIN user_film_likes likes " +
                "ON f.id = likes.film_id " +
                "LEFT JOIN film_mpa_rating fmr " +
                "ON f.rating_mpa_id = fmr.id " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(likes.user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(topFilms, this::makeFilm, count);
    }

    public Set<Integer> getLikesById(int id) {
        SqlRowSet filmLikesSql = jdbcTemplate.queryForRowSet("select USER_ID from USER_FILM_LIKES where FILM_ID = ?", id);
        Set<Integer> filmLikes = new HashSet<>();
        while (filmLikesSql.next()) {
            filmLikes.add(filmLikesSql.getInt("user_id"));
        }
        return filmLikes;
    }
}