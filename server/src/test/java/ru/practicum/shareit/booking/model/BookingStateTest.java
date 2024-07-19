package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingStateTest {

    @Test
    void parseState() {
        assertEquals(BookingState.ALL, BookingState.parseState("ALL"));
        assertEquals(BookingState.ALL, BookingState.parseState(null));
        assertEquals(BookingState.FUTURE, BookingState.parseState("FUTURE"));
        assertThrows(ValidationException.class, () -> BookingState.parseState("unknown state"));
    }
}