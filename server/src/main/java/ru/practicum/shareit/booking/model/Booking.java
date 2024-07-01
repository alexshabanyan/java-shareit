package ru.practicum.shareit.booking.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static ru.practicum.shareit.utils.SqlHelper.*;

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
