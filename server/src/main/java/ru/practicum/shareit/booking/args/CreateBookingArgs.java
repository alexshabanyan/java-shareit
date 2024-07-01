package ru.practicum.shareit.booking.args;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class CreateBookingArgs {
    Long itemId;

    LocalDateTime start;

    LocalDateTime end;

    Long bookerId;
}
