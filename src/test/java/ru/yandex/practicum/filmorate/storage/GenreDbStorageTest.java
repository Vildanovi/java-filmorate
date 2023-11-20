package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void addGenre() {
        Set<Genre> filmGenre = new HashSet<>();
        filmGenre.add(new Genre(1, "Комедия"));
        Film film1 = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1967,3,25))
                .mpa(FilmMpaRating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .likes(new HashSet<>())
                .genres(filmGenre)
                .duration(200)
                .build();
        GenreDbStorage genreDbStorage = new GenreDbStorage(jdbcTemplate);
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        filmDbStorage.addFilm(film1);
        genreDbStorage.addGenre(film1);
        Set<Genre> filmGenres = filmDbStorage.getFilmGenre(film1.getId());
        assertThat(filmGenres.size())
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(1);
    }
}
