package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utils.Headers;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private final BookingMapper bookingMapper;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                    @RequestBody CreateBookingDto createBookingDto) {
        log.info("Создание бронирования booking={}, userId={}", createBookingDto, userId);
        Booking booking = bookingService.createBooking(bookingMapper.toCreateBookingArgs(createBookingDto, userId));
        return bookingMapper.toDto(booking);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                 @PathVariable Long bookingId) {
        log.info("Получение информации о бронировании bookingId={}, userId={}", bookingId, userId);
        return bookingMapper.toDto(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public Collection<BookingDto> getBookingsByState(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                     @RequestParam Integer from,
                                                     @RequestParam Integer size,
                                                     @RequestParam(required = false) String state) {
        log.info("Получение списка бронирований пользователя userId={}, фильтр по state={}", userId, state);
        return bookingService.getBookingsForUser(userId, BookingState.parseState(state), from, size)
                .stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public Collection<BookingDto> getBookingsByStateOwner(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                          @RequestParam Integer from,
                                                          @RequestParam Integer size,
                                                          @RequestParam(required = false) String state) {
        log.info("Получение списка бронирований владельцем userId={}, фильтр по state={}", userId, state);
        return bookingService.getBookingsForItemOwner(userId, BookingState.parseState(state), from, size)
                .stream().map(bookingMapper::toDto).collect(Collectors.toList());
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBookingStatus(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam boolean approved) {
        log.info("Обновлении статуса бронировании userId={}, bookingId={}, approved={}", userId, bookingId, approved);
        return bookingMapper.toDto(bookingService.updateBookingStatus(userId, bookingId, approved));
    }
}
