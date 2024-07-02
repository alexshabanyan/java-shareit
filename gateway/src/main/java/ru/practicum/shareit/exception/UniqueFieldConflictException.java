package ru.practicum.shareit.exception;

public class UniqueFieldConflictException extends RuntimeException {
    public UniqueFieldConflictException(String message) {
        super(message);
    }
}
