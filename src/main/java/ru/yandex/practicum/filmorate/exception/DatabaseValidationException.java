package ru.yandex.practicum.filmorate.exception;

public class DatabaseValidationException extends RuntimeException {
    public DatabaseValidationException(String message) {
        super(message);
    }
}
