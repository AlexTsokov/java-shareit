package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> getBookingsByItemOwnerIdOrderByStartDesc(Long itemOwnerId);

    List<Booking> getBookingsByItemOwnerIdOrderByStartDesc(Long itemOwnerId, Pageable pageable);

    List<Booking> getBookingsByBooker_IdOrderByStartDesc(Long bookerId);

    List<Booking> getBookingsByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    List<Booking> getBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status);

    List<Booking> getBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                  LocalDateTime start,
                                                                                  LocalDateTime end);

    List<Booking> getBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                  LocalDateTime start,
                                                                                  LocalDateTime end,
                                                                                  Pageable pageable);

    List<Booking> getBookingsByBooker_IdAndStartAfterAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end);

    List<Booking> getBookingsByBooker_IdAndStartAfterAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end,
                                                                                 Pageable pageable);

    List<Booking> getBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status, Pageable pageable);

    List<Booking> getBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(Long bookerId,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end);

    List<Booking> getBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(Long bookerId,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end,
                                                                                   Pageable pageable);

    List<Booking> getBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                    LocalDateTime start,
                                                                                    LocalDateTime end);

    List<Booking> getBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                    LocalDateTime start,
                                                                                    LocalDateTime end,
                                                                                    Pageable pageable);

    List<Booking> getBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long bookerId, Status status);

    List<Booking> getBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(Long bookerId,
                                                                          Status status,
                                                                          Pageable pageable);

    List<Booking> getBookingsByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long bookerId,
                                                                                     LocalDateTime start,
                                                                                     LocalDateTime end);

    List<Booking> getBookingsByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(Long bookerId,
                                                                                     LocalDateTime start,
                                                                                     LocalDateTime end,
                                                                                     Pageable pageable);

    List<Booking> getBookingsByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end);

    List<Booking> getBookingsByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(Long bookerId,
                                                                                   LocalDateTime start,
                                                                                   LocalDateTime end,
                                                                                   Pageable pageable);

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