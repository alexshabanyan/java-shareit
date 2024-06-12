package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface CustomBookingRepository {
    <T> List<T> findLastBookingWithStatus(Set<Long> itemIds, LocalDateTime now, BookingStatus status, Class<T> type);

    <T> List<T> findNextBookingWithStatus(Set<Long> itemIds, LocalDateTime now, BookingStatus status, Class<T> type);
}
