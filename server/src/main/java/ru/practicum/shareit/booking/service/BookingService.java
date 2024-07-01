package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.args.CreateBookingArgs;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BookingService {
    Booking createBooking(CreateBookingArgs createBookingArgs);

    Booking updateBookingStatus(Long userId, Long bookingId, boolean approved);

    Booking getBooking(Long userId, Long bookingId);

    List<Booking> getBookingsForUser(Long userId, BookingState bookingState, int from, int size);

    List<Booking> getBookingsForItemOwner(Long userId, BookingState bookingState, int from, int size);

    Map<Long, List<Booking>> getItemLastBookingMapping(Set<Long> itemIds);

    Map<Long, List<Booking>> getItemNextBookingMapping(Set<Long> itemIds);
}
