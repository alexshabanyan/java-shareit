package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class CustomBookingRepositoryImpl implements CustomBookingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <T> List<T> findLastBookingWithStatus(Set<Long> itemIds, LocalDateTime now,
                                                 BookingStatus status, Class<T> type) {
        String sql = "SELECT DISTINCT ON (item_id) bk.* " +
                "FROM bookings bk " +
                "WHERE bk.item_id IN ?1 AND (bk.date_end < ?2 OR bk.date_start < ?3 AND bk.date_end > ?4) AND bk.status = ?5 " +
                "ORDER BY bk.date_end DESC";
        return entityManager.createNativeQuery(sql, type)
                .setParameter(1, itemIds)
                .setParameter(2, now)
                .setParameter(3, now)
                .setParameter(4, now)
                .setParameter(5, status.toString())
                .getResultList();
    }

    @Override
    public <T> List<T> findNextBookingWithStatus(Set<Long> itemIds, LocalDateTime now,
                                                 BookingStatus status, Class<T> type) {
        String sql = "SELECT DISTINCT ON (item_id) bk.* " +
                "FROM bookings bk " +
                "WHERE bk.item_id IN ?1 AND bk.date_start > ?2 AND bk.status = ?3 " +
                "ORDER BY bk.date_start";
        return entityManager.createNativeQuery(sql, type)
                .setParameter(1, itemIds)
                .setParameter(2, now)
                .setParameter(3, status.toString())
                .getResultList();
    }
}