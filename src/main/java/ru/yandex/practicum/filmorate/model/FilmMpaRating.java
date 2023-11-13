package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class FilmMpaRating {

    private int id; // Идентификатор
    @Size(max = 10)
    private String name; // Название рейтинга
//    @Size(max = 255)
//    private String description; // Описание возрастного рейтинга

    public FilmMpaRating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
