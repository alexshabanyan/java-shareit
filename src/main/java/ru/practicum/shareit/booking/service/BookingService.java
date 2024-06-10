package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.Collection;

public interface BookingService {
    BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto);

    BookingDto updateBookingStatus(Long userId, Long bookingId, boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    Collection<BookingDto> getBookingsForUser(Long userId, BookingState bookingState);

    Collection<BookingDto> getBookingsForItemOwner(Long userId, BookingState bookingState);
}
