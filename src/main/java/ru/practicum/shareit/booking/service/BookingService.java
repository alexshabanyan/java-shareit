package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookingService {
    Booking createBooking(CreateBookingDto createBookingDto, Long userId);

    Booking updateBookingStatus(Long userId, Long bookingId, boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getBookingsForUser(Long userId, BookingState bookingState, int from, int size);

    List<Booking> getBookingsForItemOwner(Long userId, BookingState bookingState, int from, int size);

    Map<Long, List<Booking>> getItemLastBookingMapping(Set<Long> itemIds);

    Map<Long, List<Booking>> getItemNextBookingMapping(Set<Long> itemIds);
}
