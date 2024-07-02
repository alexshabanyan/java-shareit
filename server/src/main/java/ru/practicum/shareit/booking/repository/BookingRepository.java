package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, CustomBookingRepository {
    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserAll(Long bookerId, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId AND bk.start > :start ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserFuture(Long bookerId, LocalDateTime start, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId AND bk.end < :end ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserPast(Long bookerId, LocalDateTime end, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId AND bk.start < :start AND bk.end > :end ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserCurrent(Long bookerId, LocalDateTime start, LocalDateTime end, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE u.id = :bookerId AND bk.status = :status ORDER BY bk.start DESC")
    List<Booking> findBookingsForUserByStatus(Long bookerId, BookingStatus status, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerAll(Long ownerId, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.start > :start ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerFuture(Long ownerId, LocalDateTime start, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.end < :end ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerPast(Long ownerId, LocalDateTime end, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.start < :start AND bk.end > :end ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerCurrent(Long ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    @Query("SELECT bk FROM Booking bk JOIN FETCH bk.item i JOIN FETCH bk.booker u " +
            "WHERE i.ownerId = :ownerId AND bk.status = :status ORDER BY bk.start DESC")
    List<Booking> findBookingsForItemOwnerStatus(Long ownerId, BookingStatus status, Pageable page);

    List<Booking> findAllByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime dateTime);
}
