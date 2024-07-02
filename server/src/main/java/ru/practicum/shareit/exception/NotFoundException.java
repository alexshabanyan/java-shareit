package ru.practicum.shareit.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final Long id;

    public NotFoundException(Long id) {
        this.id = id;
    }

    public NotFoundException(Long id, String message) {
        super(message);
        this.id = id;
    }
}
