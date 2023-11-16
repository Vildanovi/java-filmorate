package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final Logger log = LoggerFactory.getLogger(FilmDbStorage.class);
    private final JdbcTemplate jdbcTemplate;
    private final MpaDbStorage mpaDbStorage;
    private final GenreStorage genreStorage;

//    @Autowired
//    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }

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
                film.getMpa().getId()
        );
        if (film.getGenres() != null) {
            genreStorage.addGenre(film);
        }
        return getFilmByID(film.getId())
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + film));
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
        if (film.getGenres() != null) {
            genreStorage.addGenre(film);
        }
        return getFilmByID(film.getId())
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + film));
    }

    @Override
    public List<Film> getAll() {
        String query = "SELECT * FROM films";
        return jdbcTemplate.query(query, this::makeFilm);
    }

    @Override
    public Optional<Film> getFilmByID(int id) {
        String sqlQuery = "select * from FILMS where id = ?";
        return Optional.ofNullable(jdbcTemplate.query(sqlQuery, this::makeFilm, id).stream().findAny()
                .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id)));
//        Film film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
//        if(film != null) {
//            log.info("Найден фильм: {} {}", film.getId(), film.getName());
//            return Optional.of(film);
//        } else {
//            log.info("Фильм с идентификатором {} не найден", id);
//            return Optional.empty();
//        }
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

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .likes(getLikesById(rs.getInt("id")))
                .mpa(mpaDbStorage.getMpaById(rs.getInt("rating_mpa_id")))
                .genres(genreStorage.getFilmGenre(rs.getInt("id")))
                .build();

//        Film film = new Film();
//        film.setId(rs.getInt("id"));
//        film.setName(rs.getString("name"));
//        film.setDescription(rs.getString("description"));
//        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
//        film.setDuration(rs.getInt("duration"));
//        film.setLikes(getLikesById(rs.getInt("id")));

//        film.setMpa(resultSet.getObject("film_mpa_rating", FilmMpaRating.class));
//        film.setMpa(getMpaByFilmId(resultSet.getInt("id")));
//        film.setGenres(getFilmGenre(resultSet.getInt("id")));
    }

//    public FilmMpaRating getMpaByFilmId(int id) {
//        SqlRowSet filmMpaSql = jdbcTemplate.queryForRowSet("select rating_mpa_id from FILMS where id = ?", id);
//        if (filmMpaSql.next()) {
//            getMpaById(filmMpaSql.getInt("rating_mpa_id"));
//        }
//        return null;
//    }

    public List<Film> getPopular(int count) {
//        String popularFilms = "SELECT film_id, COUNT(user_id) FROM user_film_likes GROUP BY film_id ORDER BY COUNT(user_id) DESC LIMIT ?";

          String topFilms = "SELECT id, name, description, release_date, duration, rating_mpa_id  \n" +
                  "FROM films \n" +
                  "LEFT JOIN user_film_likes likes\n" +
                  "ON films.id = likes.film_id\n" +
                  "GROUP BY id\n" +
                  "ORDER BY COUNT(likes.user_id) DESC\n" +
                  "LIMIT ?";
//        String topFilms = "SELECT id, name, description, release_date, duration, rating_mpa_id " +
//                "FROM films " +
//                "LEFT JOIN (SELECT film_id FROM user_film_likes GROUP BY film_id ORDER BY COUNT(user_id) DESC) as rate ON films.id = rate.film_id LIMIT ?";
        return jdbcTemplate.query(topFilms, this::makeFilm, count);

//        SqlRowSet filmPopularSql = jdbcTemplate.queryForRowSet("SELECT film_id, " +
//                "COUNT(user_id) " +
//                "FROM user_film_likes " +
//                "GROUP BY film_id " +
//                "ORDER BY COUNT(user_id) DESC " +
//                "LIMIT ?;", count);
//        List<Film> filmPopular = new ArrayList<>();
//        while (filmPopularSql.next()) {
//            int filmId = filmPopularSql.getInt("film_id");
//            filmPopular.add(getFilmByID(filmId)
//                    .orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + filmId)));
//        }
//        return filmPopular;
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