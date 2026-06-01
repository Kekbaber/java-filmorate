package ru.yandex.practicum.filmorate.exception.model;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
