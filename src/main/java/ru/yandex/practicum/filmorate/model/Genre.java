package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Size;

@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Genre {

    @EqualsAndHashCode.Include
    private int id;
    @Size(max =  50)
    private String name;

    public Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
