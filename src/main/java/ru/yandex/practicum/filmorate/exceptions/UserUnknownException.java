package ru.yandex.practicum.filmorate.exceptions;

public class UserUnknownException extends RuntimeException {

    public UserUnknownException(String message) {
        super(message);
    }
}
