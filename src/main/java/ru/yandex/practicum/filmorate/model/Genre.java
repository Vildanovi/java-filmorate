package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class Genre {

    @NotNull
    private int id;
    @Size(max =  50)
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}