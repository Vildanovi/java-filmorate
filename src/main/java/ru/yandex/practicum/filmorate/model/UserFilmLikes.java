package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserFilmLikes {

    private int filmId;
    private int userId;

    public UserFilmLikes(int filmId, int userId) {
        this.filmId = filmId;
        this.userId = userId;
    }
}
