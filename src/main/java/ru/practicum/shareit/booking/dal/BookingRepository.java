package ru.practicum.shareit.booking.dal;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
            SELECT COUNT(b) > 0
            FROM Booking b
            WHERE b.item.id = ?1
            AND b.status = APPROVED
            AND (b.start <= ?3 AND b.end >= ?2)
            """)
    Boolean isTimeCrossing(long itemId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerId(long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(long bookerId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatus(long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerId(long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(long ownerId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(long ownerId, BookingStatus status, Sort sort);
}
