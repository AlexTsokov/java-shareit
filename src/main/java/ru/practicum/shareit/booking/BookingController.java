package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.NotBlank;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookerId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public Booking change(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long bookingId,
                          @NotBlank @RequestParam Boolean approved) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingList(@RequestHeader("X-Sharer-User-Id") Long bookerId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingList(bookerId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingByOwner(ownerId, state);
    }
}
