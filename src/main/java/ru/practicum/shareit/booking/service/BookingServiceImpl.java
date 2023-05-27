package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
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
    public List<BookingDtoWithItemName> findAllByUser(Long bookerId, String state, Integer page, Integer size) {
        validateParams(bookerId, page, size);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();
        if (page == null || size == null) {
            switch (checkState(state)) {
                case ALL:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdOrderByStartDesc(bookerId));
                case REJECTED:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(bookerId, Status.REJECTED));
                case WAITING:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(bookerId, Status.WAITING));
                case CURRENT:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                                    bookerId, end, start));
                case PAST:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(bookerId,
                                    end, start));
                case FUTURE:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStartAfterAndEndAfterOrderByStartDesc(bookerId,
                                    end, start));
                default:
                    throw new StateException("Unknown state: " + state);
            }
        } else {
            int fromPage = page / size;
            Pageable pageable = PageRequest.of(fromPage, size);
            switch (checkState(state)) {
                case ALL:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdOrderByStartDesc(bookerId, pageable));
                case REJECTED:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(
                                    bookerId, Status.REJECTED, pageable));
                case WAITING:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStatusEqualsOrderByStartDesc(
                                    bookerId, Status.WAITING, pageable));
                case CURRENT:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                                    bookerId, end, start, pageable));
                case PAST:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStartBeforeAndEndBeforeOrderByStartDesc(
                                    bookerId, end, start, pageable));
                case FUTURE:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByBooker_IdAndStartAfterAndEndAfterOrderByStartDesc(
                                    bookerId, end, start, pageable));
                default:
                    throw new StateException("Unknown state: " + state);
            }
        }
    }

    @Override
    @Transactional
    public List<BookingDtoWithItemName> findAllByOwner(Long ownerId, String state, Integer page, Integer size) {
        validateParams(ownerId, page, size);
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.now();
        if (page == null || size == null) {
            switch (checkState(state)) {
                case ALL:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdOrderByStartDesc(ownerId));
                case REJECTED:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                                    ownerId, Status.REJECTED));
                case WAITING:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                                    ownerId, Status.WAITING));
                case CURRENT:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                    ownerId, end, start));
                case PAST:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
                                    ownerId, end, start));
                case FUTURE:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(
                                    ownerId, end, start));
                default:
                    throw new StateException("Unknown state: " + state);
            }
        } else {
            int fromPage = page / size;
            Pageable pageable = PageRequest.of(fromPage, size);
            switch (checkState(state)) {
                case ALL:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdOrderByStartDesc(ownerId, pageable));
                case REJECTED:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                                    ownerId, Status.REJECTED, pageable));
                case WAITING:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStatusEqualsOrderByStartDesc(
                                    ownerId, Status.WAITING, pageable));
                case CURRENT:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
                                    ownerId, end, start, pageable));
                case PAST:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStartBeforeAndEndBeforeOrderByStartDesc(
                                    ownerId, end, start, pageable));
                case FUTURE:
                    return BookingMapper.toBookingWithItemNameDtoList(
                            bookingRepository.getBookingsByItemOwnerIdAndStartAfterAndEndAfterOrderByStartDesc(
                                    ownerId, end, start, pageable));
                default:
                    throw new StateException("Unknown state: " + state);
            }
        }
    }

    private void validateParams(Long userId, int page, int size) {
        if (!(userId != null && userRepository.findById(userId).isPresent()))
            throw new EntityNotFoundException("Пользователь не найден");
        if (page < 0 || size < 1)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
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
