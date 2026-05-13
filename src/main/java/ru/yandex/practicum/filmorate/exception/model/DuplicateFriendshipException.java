package ru.yandex.practicum.filmorate.exception.model;

public class DuplicateFriendshipException extends RuntimeException {
    public DuplicateFriendshipException(String message) {
        super(message);
    }
}
