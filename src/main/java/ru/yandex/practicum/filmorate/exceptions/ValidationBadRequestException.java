package ru.yandex.practicum.filmorate.exceptions;

public class ValidationBadRequestException extends RuntimeException {

    public ValidationBadRequestException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
