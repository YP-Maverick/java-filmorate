package ru.yandex.practicum.filmorate.exception;

public class IdNotAllowedException extends RuntimeException {
    public IdNotAllowedException(String message) {
        super(message);
    }
}
