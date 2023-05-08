package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public Booking createBooking(Long bookerId, Booking booking) {
        if (!validate(booking)) {
            throw new BookingNotFoundException("Бронирование невозможно");
        }
        if (bookerId == (long) booking.getItem().getOwnerId()) {
            throw new EntityNotFoundException("Вы не можете арендовать свою вещь");
        }
        itemService.checkItem(booking.getItem().getId());
        if (!userService.checkUserExist(bookerId))
            throw new EntityNotFoundException("Пользователь не найден");
        booking.setBooker(userService.findUserById(bookerId));
        log.info("Бронирование создано");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking updateBooking(Long ownerId, Long bookingId, Boolean approved) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(EntityNotFoundException::new);
        if ((long) booking.getItem().getOwnerId() != ownerId) {
            throw new EntityNotFoundException("У пользовтеля нет этого предмета");
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new BookingNotFoundException("Бронирование невозможно");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        log.info("Бронирование обновлено");
        return bookingRepository.save(booking);
    }

    @Override
    public Booking getBookingById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!userService.checkUserExist(userId))
            throw new EntityNotFoundException("Пользователь не найден");
        if (userId != (long) booking.getBooker().getId() && userId != (long) booking.getItem().getOwnerId()) {
            throw new EntityNotFoundException("У пользователя нет этого бронирования");
        }
        return booking;
    }

    @Override
    public List<Booking> getBookingList(Long bookerId, String state) {
        if (!userService.checkUserExist(bookerId))
            throw new EntityNotFoundException("Пользователь не найден");
        checkState(state);
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByBooker_Id(bookerId, Sort.by("start").descending());
            case CURRENT:
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                        bookerId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").descending());
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, LocalDateTime.now(),
                        Sort.by("start").descending());
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, LocalDateTime.now(),
                        Sort.by("start").descending());
            case WAITING:
                return bookingRepository.findByBooker_IdAndStatusEquals(bookerId, Status.WAITING,
                        Sort.by("start").descending());
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStatusEquals(bookerId, Status.REJECTED,
                        Sort.by("start").descending());
            default:
                throw new StateException("Unknown state: " + state);
        }
    }

    @Override
    public List<Booking> getBookingByOwner(Long ownerId, String state) {
        if (!userService.checkUserExist(ownerId))
            throw new EntityNotFoundException("Пользователь не найден");
        checkState(state);
        List<Item> itemList = itemRepository.findItemsByUser(ownerId);
        List<Long> itemIdList = new ArrayList<>();
        for (Item item : itemList) {
            itemIdList.add(item.getId());
        }
        switch (State.valueOf(state)) {
            case ALL:
                return bookingRepository.findByItem_IdIn(itemIdList, Sort.by("start").descending());
            case CURRENT:
                return bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(itemIdList,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").descending());
            case PAST:
                return bookingRepository.findByItem_IdInAndEndIsBefore(itemIdList, LocalDateTime.now(),
                        Sort.by("start").descending());
            case FUTURE:
                return bookingRepository.findByItem_IdInAndStartIsAfter(itemIdList, LocalDateTime.now(),
                        Sort.by("start").descending());
            case WAITING:
                return bookingRepository.findByItem_IdInAndStatusEquals(itemIdList, Status.WAITING,
                        Sort.by("start").descending());
            case REJECTED:
                return bookingRepository.findByItem_IdInAndStatusEquals(itemIdList, Status.REJECTED,
                        Sort.by("start").descending());
            default:
                throw new StateException("Unknown state: " + state);
        }
    }

    private void checkState(String state) {
        try {
            State.valueOf(state);
        } catch (Exception e) {
            throw new StateException("Unknown state: " + state);
        }
    }

    public boolean validate(Booking booking) {
        LocalDateTime dateNow = LocalDateTime.now();
        return booking.getEnd() != null && booking.getStart() != null && !booking.getEnd().isBefore(dateNow) &&
                !booking.getStart().isBefore(LocalDateTime.now()) && booking.getStart().isBefore(booking.getEnd()) &&
                !booking.getStart().isEqual(booking.getEnd());
    }
}
