package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmMpaRating;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import java.time.LocalDate;
import java.util.HashSet;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;

    @Test
    public void testFindFilmById() {
        Film film1 = Film.builder()
                .name("Name")
                .description("Description")
                .releaseDate(LocalDate.of(1967,3,25))
                .mpa(FilmMpaRating.builder()
                        .id(1)
                        .name("G")
                        .build())
                .genres(new HashSet<>())
                .duration(200)
                .build();
        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate);
        Film newFilm = filmDbStorage.addFilm(film1);
        int id = newFilm.getId();


        Film savedFilm = filmDbStorage.getFilmByID(id).orElseThrow(() -> new EntityNotFoundException("Фильм с указанным id не найден: " + id));

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film1);
    }
}
