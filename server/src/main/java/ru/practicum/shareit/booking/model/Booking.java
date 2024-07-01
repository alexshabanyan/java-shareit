package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.SqlHelper.BOOKING_BOOKER_ID;
import static ru.practicum.shareit.utils.SqlHelper.BOOKING_DATE_END;
import static ru.practicum.shareit.utils.SqlHelper.BOOKING_DATE_START;
import static ru.practicum.shareit.utils.SqlHelper.BOOKING_ID;
import static ru.practicum.shareit.utils.SqlHelper.BOOKING_ITEM_ID;
import static ru.practicum.shareit.utils.SqlHelper.BOOKING_STATUS;
import static ru.practicum.shareit.utils.SqlHelper.SCHEMA;
import static ru.practicum.shareit.utils.SqlHelper.TABLE_BOOKINGS;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = TABLE_BOOKINGS, schema = SCHEMA)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = BOOKING_ID)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = BOOKING_ITEM_ID)
    private Item item;

    @Enumerated(EnumType.STRING)
    @Column(name = BOOKING_STATUS)
    private BookingStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = BOOKING_BOOKER_ID)
    private User booker;

    @Column(name = BOOKING_DATE_START)
    private LocalDateTime start;

    @Column(name = BOOKING_DATE_END)
    private LocalDateTime end;
}
