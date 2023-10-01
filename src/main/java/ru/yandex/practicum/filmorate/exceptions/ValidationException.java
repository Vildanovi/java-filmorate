package ru.yandex.practicum.filmorate.exceptions;

import java.io.IOException;

public class ValidationException extends IOException {

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
