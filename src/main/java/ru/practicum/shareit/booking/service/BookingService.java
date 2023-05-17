package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemName;

import java.util.List;


public interface BookingService {
    BookingDtoWithItemName createBooking(Long userId, BookingDto booking);

    BookingDtoWithItemName updateBooking(Long ownerId, Long bookingId, Boolean approved);

    BookingDtoWithItemName getBookingById(Long userId, Long id);

    List<BookingDtoWithItemName> getBookingList(Long bookerId, String state);

    List<BookingDtoWithItemName> getBookingByOwner(Long ownerId, String state);
}
