package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import ru.yandex.practicum.filmorate.interfaces.MinimumDate;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
//@SuperBuilder
public class Film {

    private int id; // целочисленный идентификатор
    @NotBlank private
    String name; // название
    @NotNull @Size(max = 200)
    private String description; // описание
    @NotNull(message = "Дата релиза не может быть Null")
    @MinimumDate(message = "Дата релиза не может быть ранее 28 декабря 1895 года")
    private LocalDate releaseDate; // дата релиза
    @Positive(message = "Продолжительность не может быть меньше 0")
    private int duration; // продолжительность фильма
    @JsonIgnore
    private Set<Integer> likes = new HashSet<>(); //id пользователей кто поставил лайк
}
