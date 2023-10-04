package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class Film {

    private int id; // целочисленный идентификатор
    @NotBlank private String name; // название
    @Size(max = 200) private String description; // описание
    private LocalDate releaseDate; // дата релиза
    @Positive(message = "Продолжительность не может быть меньше 0") private int duration; // продолжительность фильма
}
