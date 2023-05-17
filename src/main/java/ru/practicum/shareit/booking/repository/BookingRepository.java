package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStatusEquals(Long bookerId, Status status, Sort sort);

    List<Booking> findByItem_IdIn(List<Long> itemId, Sort sort);

    List<Booking> findByItem_IdInAndEndIsBefore(List<Long> itemId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_IdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItem_IdInAndStartIsAfter(List<Long> itemId, LocalDateTime start, Sort sort);

    List<Booking> findByItem_IdInAndStatusEquals(List<Long> itemId, Status status, Sort sort);

    @Query(value = "select * from bookings b " +
            "where item_id = ?1 " +
            "and start_date < current_timestamp " +
            "order by start_date desc " +
            "limit 1",
            nativeQuery = true)

    Booking findLastBookingByItemId(Long itemId);

    @Query(value = "select * from bookings b " +
            "where item_id = ?1 " +
            "and status in ('WAITING', 'APPROVED') " +
            "and start_date > current_timestamp " +
            "order by start_date asc " +
            "limit 1",
            nativeQuery = true)

    Booking findNextBookingByItemId(Long itemId);

    @Query(value = "select * from bookings b " +
            "where item_id = ?1 " +
            "and booker_id = ?2 " +
            "and end_date <= current_timestamp " +
            "order by end_date desc " +
            "limit 1",
            nativeQuery = true)
    Booking findLastBookingByBookerId(Long itemId, Long userId);
}
