package ru.yandex.practicum.filmorate.model;

import lombok.*;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilmMpaRating {

    private int id; // Идентификатор
    @Size(max = 10)
    private String name; // Название рейтинга

    public FilmMpaRating(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
