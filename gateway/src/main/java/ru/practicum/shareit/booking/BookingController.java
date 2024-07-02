package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.CreateBookingDto;
import ru.practicum.shareit.utils.Headers;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                @RequestBody @Valid CreateBookingDto createBookingDto) {
        log.info("Создание бронирования booking={}, userId={}", createBookingDto, userId);
        return bookingClient.createBooking(userId, createBookingDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получение информации о бронировании bookingId={}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByState(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                     @RequestParam(required = false) String state,
                                                     @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение списка бронирований пользователя userId={}, фильтр по state={}", userId, state);
        return bookingClient.getBookingsByState(userId, BookingState.parseState(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByStateOwner(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                          @RequestParam(required = false) String state,
                                                          @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Получение списка бронирований владельцем userId={}, фильтр по state={}", userId, state);
        return bookingClient.getBookingsByStateOwner(userId, BookingState.parseState(state), from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBookingStatus(@RequestHeader(Headers.HEADER_USER_ID) Long userId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam boolean approved) {
        log.info("Обновлении статуса бронировании userId={}, bookingId={}, approved={}", userId, bookingId, approved);
        return bookingClient.updateBookingStatus(userId, bookingId, approved);
    }
}
