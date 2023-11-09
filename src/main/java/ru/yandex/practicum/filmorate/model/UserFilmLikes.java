package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserFilmLikes {

    private int filmId;
    private int userId;
//    private LocalDateTime createdDate;

    public UserFilmLikes(int filmId, int userId) {
        this.filmId = filmId;
        this.userId = userId;
    }
}
