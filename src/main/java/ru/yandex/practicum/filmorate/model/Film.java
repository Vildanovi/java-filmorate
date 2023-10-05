package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.interfaces.MinimumDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {

    private int id; // целочисленный идентификатор
    @NotBlank private
    String name; // название
    @NotNull @Size(max = 200)
    private String description; // описание
    @NotNull(message = "Дата релиза не может быть Null")
    @MinimumDate(message = "Дата релиза не может быть ранее 28 декабря 1895 года")
    @PastOrPresent(message = "Дата релиза релиза не может быть позже текущей")
    private LocalDate releaseDate; // дата релиза
    @Positive(message = "Продолжительность не может быть меньше 0")
    private int duration; // продолжительность фильма
}
