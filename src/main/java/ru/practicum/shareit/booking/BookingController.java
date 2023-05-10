package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    @PostMapping
    public Booking create(@NotNull @RequestHeader("X-Sharer-User-Id") Long bookerId,
                          @Valid @RequestBody BookingDto bookingDto) {
        Booking booking = BookingMapper.mapToBooking(bookingDto,
                itemService.findItemById(bookingDto.getItemId()),
                userService.findUserById(bookerId));
        return bookingService.createBooking(bookerId, booking);
    }

    @PatchMapping("/{bookingId}")
    public Booking change(@NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                          @PathVariable Long bookingId,
                          @NotBlank @RequestParam Boolean approved) {
        return bookingService.updateBooking(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                  @NotNull @PathVariable Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping
    public List<Booking> getBookingList(@NotNull @RequestHeader("X-Sharer-User-Id") Long bookerId,
                                        @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingList(bookerId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getBookingByOwner(@NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingByOwner(ownerId, state);
    }
}
