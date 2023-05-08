package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Booking booking);

    Booking updateBooking(Long ownerId, Long bookingId, Boolean approved);

    Booking getBookingById(Long userId, Long id);

    List<Booking> getBookingList(Long bookerId, String state);

    List<Booking> getBookingByOwner(Long ownerId, String state);
}
