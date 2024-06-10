package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookingForItemExtendDto {
    @EqualsAndHashCode.Include
    Long id;

    Long bookerId;

    @JsonIgnore
    Long itemId;

    BookingStatus status;

    LocalDateTime start;

    LocalDateTime end;
}
