package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingCreateDto bookingCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Item item = itemRepository.findById(bookingCreateDto.getItemId())
                .orElseThrow(() -> new NotFoundException(bookingCreateDto.getItemId()));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования");
        }
        if (Objects.equals(item.getOwnerId(), userId)) {
            throw new NotFoundException(item.getId(), "Невозможно забронировать свой предмет");
        }
        Booking booking = bookingMapper.toModel(bookingCreateDto, user, item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long userId, Long bookingId, boolean approved) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(bookingId));
        if (!Objects.equals(userId, booking.getItem().getOwnerId())) {
            throw new NotFoundException(bookingId);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования изменить уже нельзя");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(Long userId, Long bookingId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(bookingId));
        if (!Objects.equals(userId, booking.getBooker().getId()) &&
                !Objects.equals(userId, booking.getItem().getOwnerId())) {
            throw new NotFoundException(bookingId);
        }
        return bookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getBookingsForUser(Long userId, BookingState state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsForUserAll(userId);
                break;
            case CURRENT:
                result = bookingRepository.findBookingsForUserCurrent(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findBookingsForUserPast(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findBookingsForUserFuture(userId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findBookingsForUserByStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findBookingsForUserByStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                log.warn("Неизвестный параметр фильтрации state={}", state);
                return List.of();
        }
        return bookingMapper.toDto(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDto> getBookingsForItemOwner(Long userId, BookingState state) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        List<Booking> result;
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsForItemOwnerAll(userId);
                break;
            case CURRENT:
                result = bookingRepository.findBookingsForItemOwnerCurrent(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                result = bookingRepository.findBookingsForItemOwnerPast(userId, LocalDateTime.now());
                break;
            case FUTURE:
                result = bookingRepository.findBookingsForItemOwnerFuture(userId, LocalDateTime.now());
                break;
            case WAITING:
                result = bookingRepository.findBookingsForItemOwnerStatus(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findBookingsForItemOwnerStatus(userId, BookingStatus.REJECTED);
                break;
            default:
                log.warn("Неизвестный параметр фильтрации state={}", state);
                return List.of();
        }
        return bookingMapper.toDto(result);
    }
}
