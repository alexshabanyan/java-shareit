package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.args.CreateBookingArgs;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookingServiceImplTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    private User booker;
    private User owner;
    private User otherUser;
    private Item item;

    @BeforeAll
    void setUp() {
        booker = userRepository.save(new User(null, "User", "user@mail.com"));
        owner = userRepository.save(new User(null, "Owner", "owner@mail.com"));
        otherUser = userRepository.save(new User(null, "Other", "other@mail.com"));
        item = itemRepository.save(new Item(null, "Item", "Item desc", true, owner.getId(), null));
    }

    @AfterAll
    void afterAll() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void shouldNotCreateBookingForNotAvailableItemAndForOwnItem() {
        Item notAvailableItem = itemRepository.save(new Item(null, "Item", "Item desc", false, owner.getId(), null));
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        assertThrows(ValidationException.class, () -> bookingService.createBooking(new CreateBookingArgs(notAvailableItem.getId(), start, end, booker.getId())));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(new CreateBookingArgs(item.getId(), start, end, owner.getId())));
    }

    @ParameterizedTest
    @MethodSource("argumentsForCorrectUser")
    void onlyCorrectUserShouldSeeBooking(Long userId, boolean canFetch) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        Booking booking = bookingService.createBooking(new CreateBookingArgs(item.getId(), start, end, booker.getId()));

        boolean ableToFetch;
        try {
            Booking fetched = bookingService.getBooking(userId, booking.getId());
            assertThat(fetched, equalTo(booking));
            ableToFetch = true;
        } catch (NotFoundException e) {
            ableToFetch = false;
        }
        assertThat(ableToFetch, equalTo(canFetch));
    }

    private Stream<Arguments> argumentsForCorrectUser() {
        return Stream.of(
                Arguments.of(owner.getId(), true),
                Arguments.of(booker.getId(), true),
                Arguments.of(otherUser.getId(), false)
        );
    }

    @Test
    void updateBookingStatus() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);

        Booking bookingToApprove = bookingService.createBooking(new CreateBookingArgs(item.getId(), start, end, booker.getId()));
        assertThat(bookingToApprove.getStatus(), equalTo(BookingStatus.WAITING));
        Booking updatedBookingApproved = bookingService.updateBookingStatus(owner.getId(), bookingToApprove.getId(), true);
        assertThat(updatedBookingApproved.getStatus(), equalTo(BookingStatus.APPROVED));

        Booking bookingToReject = bookingService.createBooking(new CreateBookingArgs(item.getId(), start, end, booker.getId()));
        assertThat(bookingToReject.getStatus(), equalTo(BookingStatus.WAITING));
        Booking updatedBookingRejected = bookingService.updateBookingStatus(owner.getId(), bookingToReject.getId(), false);
        assertThat(updatedBookingRejected.getStatus(), equalTo(BookingStatus.REJECTED));


        Booking bookingToDoubleUpdate = bookingService.createBooking(new CreateBookingArgs(item.getId(), LocalDateTime.now().minusYears(1), LocalDateTime.now().minusYears(1).plusDays(1), booker.getId()));
        assertThat(bookingToDoubleUpdate.getStatus(), equalTo(BookingStatus.WAITING));
        // update by not owner
        assertThrows(NotFoundException.class, () -> bookingService.updateBookingStatus(otherUser.getId(), bookingToDoubleUpdate.getId(), true));

        bookingService.updateBookingStatus(owner.getId(), bookingToDoubleUpdate.getId(), false);
        assertThrows(ValidationException.class, () -> bookingService.updateBookingStatus(owner.getId(), bookingToDoubleUpdate.getId(), true));

    }
}