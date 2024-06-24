package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.args.CreateBookingArgs;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.utils.Pagination;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public Booking createBooking(CreateBookingArgs args) {
        User user = userRepository.findById(args.getBookerId()).orElseThrow(() -> new NotFoundException(args.getBookerId()));
        Item item = itemRepository.findById(args.getItemId()).orElseThrow(() -> new NotFoundException(args.getItemId()));
        if (!item.getAvailable()) {
            throw new ValidationException("Предмет недоступен для бронирования");
        }
        if (isOwner(user, item)) {
            throw new NotFoundException(item.getId(), "Невозможно забронировать свой предмет");
        }
        Booking booking = bookingMapper.toModel(args, item, user);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);
    }

    private static boolean isOwner(User user, Item item) {
        return Objects.equals(item.getOwnerId(), user.getId());
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(Long userId, Long bookingId, boolean approved) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(bookingId));
        if (!isOwner(user, booking.getItem())) {
            throw new NotFoundException(bookingId);
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Статус бронирования изменить уже нельзя");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBooking(Long userId, Long bookingId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException(bookingId));
        if (isNotOwnerOrBooker(userId, booking)) {
            throw new NotFoundException(bookingId);
        }
        return booking;
    }

    private static boolean isNotOwnerOrBooker(Long userId, Booking booking) {
        return !Objects.equals(userId, booking.getBooker().getId()) && !Objects.equals(userId, booking.getItem().getOwnerId());
    }

    @Override
    public List<Booking> getBookingsForUser(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        List<Booking> result;
        Pageable page = Pagination.getPage(from, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsForUserAll(userId, page);
                break;
            case CURRENT:
                result = bookingRepository.findBookingsForUserCurrent(userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case PAST:
                result = bookingRepository.findBookingsForUserPast(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                result = bookingRepository.findBookingsForUserFuture(userId, LocalDateTime.now(), page);
                break;
            case WAITING:
                result = bookingRepository.findBookingsForUserByStatus(userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                result = bookingRepository.findBookingsForUserByStatus(userId, BookingStatus.REJECTED, page);
                break;
            default:
                log.warn("Неизвестный параметр фильтрации state={}", state);
                return List.of();
        }
        return result;
    }

    @Override
    public List<Booking> getBookingsForItemOwner(Long userId, BookingState state, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
        List<Booking> result;
        Pageable page = Pagination.getPage(from, size, Sort.by("start").descending());
        switch (state) {
            case ALL:
                result = bookingRepository.findBookingsForItemOwnerAll(userId, page);
                break;
            case CURRENT:
                result = bookingRepository.findBookingsForItemOwnerCurrent(userId, LocalDateTime.now(), LocalDateTime.now(), page);
                break;
            case PAST:
                result = bookingRepository.findBookingsForItemOwnerPast(userId, LocalDateTime.now(), page);
                break;
            case FUTURE:
                result = bookingRepository.findBookingsForItemOwnerFuture(userId, LocalDateTime.now(), page);
                break;
            case WAITING:
                result = bookingRepository.findBookingsForItemOwnerStatus(userId, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                result = bookingRepository.findBookingsForItemOwnerStatus(userId, BookingStatus.REJECTED, page);
                break;
            default:
                log.warn("Неизвестный параметр фильтрации state={}", state);
                return List.of();
        }
        return result;
    }

    @Override
    public Map<Long, List<Booking>> getItemLastBookingMapping(Set<Long> itemIds) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Booking>> lastBookingMapping;
        lastBookingMapping = bookingRepository
                .findLastBookingWithStatus(itemIds, now, BookingStatus.APPROVED)
                .stream().collect(Collectors.groupingBy(b -> b.getItem().getId()));
        return lastBookingMapping;
    }

    @Override
    public Map<Long, List<Booking>> getItemNextBookingMapping(Set<Long> itemIds) {
        LocalDateTime now = LocalDateTime.now();
        Map<Long, List<Booking>> nextBookingMapping;
        nextBookingMapping = bookingRepository
                .findNextBookingWithStatus(itemIds, now, BookingStatus.APPROVED).stream()
                .collect(Collectors.groupingBy(b -> b.getItem().getId()));
        return nextBookingMapping;
    }
}
