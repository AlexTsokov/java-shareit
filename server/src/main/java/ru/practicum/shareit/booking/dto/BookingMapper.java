package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem().getId(),
                booking.getBooker().getId(),
                booking.getStatus());
    }

    public static Booking mapToBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(
        bookingDto.getId(),
        bookingDto.getStart(),
        bookingDto.getEnd(),
        item,
        user,
        Status.WAITING);
    }

    public static BookingDtoWithItemName toBookingDtoWithItemName(Booking booking, String itemName) {
        return new BookingDtoWithItemName(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus(),
                itemName);
    }

    public static List<BookingDtoWithItemName> toBookingWithItemNameDtoList(List<Booking> bookings) {
        List<BookingDtoWithItemName> dtoList = new ArrayList<>();
        for (Booking booking : bookings) {
            dtoList.add(toBookingDtoWithItemName(booking, booking.getItem().getName()));
        }
        return dtoList;
    }
}
