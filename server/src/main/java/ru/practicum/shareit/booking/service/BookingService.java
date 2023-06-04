package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemName;

import java.util.List;


public interface BookingService {
    BookingDtoWithItemName createBooking(Long userId, BookingDto booking);

    BookingDtoWithItemName updateBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDtoWithItemName getBookingById(Long userId, Long id);

    List<BookingDtoWithItemName> findAllByUser(Long bookerId, String state, Integer page, Integer size);

    List<BookingDtoWithItemName> findAllByOwner(Long ownerId, String state, Integer page, Integer size);
}
