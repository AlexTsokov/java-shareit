package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemName;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.StateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public BookingDtoWithItemName createBooking(Long bookerId, BookingDto bookingDto) {
        if (!validate(bookingDto)) throw new BookingNotFoundException("Бронирование невозможно");
        User user = userRepository.findById(bookerId).orElseThrow(EntityNotFoundException::new);
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        if (!item.getAvailable())
            throw new BookingNotFoundException("Предмет не доступен для бронирования");
        Booking booking = BookingMapper.mapToBooking(bookingDto, item, user);
        if (bookerId == (long) booking.getItem().getOwner().getId())
            throw new EntityNotFoundException("Вы не можете арендовать свою вещь");
        booking.setBooker(user);
        Booking bookingForSave = bookingRepository.save(booking);
        log.info("Бронирование для {} создано", booking.getItem().getName());
        String itemName = itemRepository.findById(bookingForSave.getItem().getId()).get().getName();
        return BookingMapper.toBookingDtoWithItemName(bookingForSave, itemName);
    }

    @Override
    @Transactional
    public BookingDtoWithItemName updateBooking(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(EntityNotFoundException::new);
        if ((long) booking.getItem().getOwner().getId() != ownerId) {
            throw new EntityNotFoundException("У пользователя " +
                    userRepository.findById(ownerId).orElseThrow(EntityNotFoundException::new).getName() +
                    "нет предмета " + booking.getItem().getName());
        }
        if (booking.getStatus() != Status.WAITING) {
            throw new BookingNotFoundException("Бронирование невозможно");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        log.info("Бронирование для {} обновлено", booking.getItem().getName());
        Booking bookingForSave = bookingRepository.save(booking);
        return BookingMapper.toBookingDtoWithItemName(bookingForSave, bookingForSave.getItem().getName());
    }

    @Override
    @Transactional
    public BookingDtoWithItemName getBookingById(Long userId, Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (!(userId != null && userRepository.findById(userId).isPresent()))
            throw new EntityNotFoundException("Пользователь не найден");
        if (userId != (long) booking.getBooker().getId() && userId != (long) booking.getItem().getOwner().getId()) {
            throw new EntityNotFoundException("У пользователя нет этого бронирования");
        }
        return BookingMapper.toBookingDtoWithItemName(booking, booking.getItem().getName());
    }

    @Override
    @Transactional
    public List<BookingDtoWithItemName> getBookingList(Long bookerId, String state) {
        if (!(bookerId != null && userRepository.findById(bookerId).isPresent()))
            throw new EntityNotFoundException("Пользователь не найден");
        switch (checkState(state)) {
            case ALL:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByBooker_Id(bookerId, Sort.by("start").descending()));
            case CURRENT:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(
                                bookerId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").descending()));
            case PAST:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByBooker_IdAndEndIsBefore(bookerId, LocalDateTime.now(),
                                Sort.by("start").descending()));
            case FUTURE:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByBooker_IdAndStartIsAfter(bookerId, LocalDateTime.now(),
                                Sort.by("start").descending()));
            case WAITING:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByBooker_IdAndStatusEquals(bookerId, Status.WAITING,
                                Sort.by("start").descending()));
            case REJECTED:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByBooker_IdAndStatusEquals(bookerId, Status.REJECTED,
                                Sort.by("start").descending()));
            default:
                throw new StateException("Unknown state: " + state);
        }
    }

    @Override
    @Transactional
    public List<BookingDtoWithItemName> getBookingByOwner(Long ownerId, String state) {
        if (!(ownerId != null && userRepository.findById(ownerId).isPresent()))
            throw new EntityNotFoundException("Пользователь не найден");
        List<Item> itemList = itemRepository.findItemsByUser(ownerId);
        List<Long> itemIdList = new ArrayList<>();
        for (Item item : itemList) {
            itemIdList.add(item.getId());
        }
        switch (checkState(state)) {
            case ALL:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByItem_IdIn(itemIdList, Sort.by("start").descending()));
            case CURRENT:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(itemIdList,
                                LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").descending()));
            case PAST:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByItem_IdInAndEndIsBefore(itemIdList, LocalDateTime.now(),
                                Sort.by("start").descending()));
            case FUTURE:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByItem_IdInAndStartIsAfter(itemIdList, LocalDateTime.now(),
                                Sort.by("start").descending()));
            case WAITING:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByItem_IdInAndStatusEquals(itemIdList, Status.WAITING,
                                Sort.by("start").descending()));
            case REJECTED:
                return BookingMapper.toBookingWithItemNameDtoList(
                        bookingRepository.findByItem_IdInAndStatusEquals(itemIdList, Status.REJECTED,
                                Sort.by("start").descending()));
            default:
                throw new StateException("Unknown state: " + state);
        }
    }

    private State checkState(String state) {
        try {
            return State.valueOf(state);
        } catch (Exception e) {
            throw new StateException("Unknown state: " + state);
        }
    }

    public boolean validate(BookingDto booking) {
        LocalDateTime dateNow = LocalDateTime.now();
        return booking.getEnd() != null && booking.getStart() != null && !booking.getEnd().isBefore(dateNow) &&
                !booking.getStart().isBefore(LocalDateTime.now()) && booking.getStart().isBefore(booking.getEnd()) &&
                !booking.getStart().isEqual(booking.getEnd());
    }
}
