package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.validation.ValidBookingInterval;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Value
@RequiredArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ValidBookingInterval(message = "Некорректное время бронирования")
public class BookingCreateDto {
    @EqualsAndHashCode.Include
    @Null
    Long id;

    @NotNull(message = "Id предмета не может отсутствовать")
    Long itemId;

    @Null
    BookingStatus status;

    @NotNull(message = "Дата начала бронирования не может отсутствовать")
    @FutureOrPresent
    LocalDateTime start;

    @NotNull(message = "Дата окончания бронирования не может отсутствовать")
    @FutureOrPresent
    LocalDateTime end;
}
