package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItemName;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {

    private final EntityManager entityManager;
    private final BookingServiceImpl service;
    private final BookingRepository bookingRepository;
    private User user1;
    private User user2;
    private Item item;

    @BeforeEach
    void beforeEach() {
        user1 = new User();
        user1.setName("name1");
        user1.setEmail("mail1@mail.ru");
        entityManager.persist(user1);

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("mail2@mail.ru");
        entityManager.persist(user2);

        item = new Item();
        item.setName("itemname");
        item.setDescription("item description");
        item.setOwner(user1);
        item.setAvailable(true);
        entityManager.persist(item);
    }

    @AfterEach
    void afterEach() {
        entityManager.createNativeQuery("truncate table users");
        entityManager.createNativeQuery("truncate table items");
        entityManager.createNativeQuery("truncate table bookings");
    }

    @Test
    void createBookingTest() {
        BookingDto bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(item.getId())
                .build();

        BookingDtoWithItemName bookingDtoWithItemName = service.createBooking(user2.getId(), bookingDto);

        Booking bookingDto1 = bookingRepository.getReferenceById(bookingDtoWithItemName.getId());

        assertEquals(bookingDtoWithItemName.getId(), bookingDto1.getId());
        assertEquals(bookingDtoWithItemName.getStart(), bookingDto1.getStart());
        assertEquals(bookingDtoWithItemName.getEnd(), bookingDto1.getEnd());
        assertEquals(bookingDtoWithItemName.getStatus(), bookingDto1.getStatus());
        assertEquals(bookingDtoWithItemName.getBooker().getId(), bookingDto1.getBooker().getId());
        assertEquals(bookingDtoWithItemName.getItem().getName(), bookingDto1.getItem().getName());
    }

    @Test
    void updateBookingTest() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusDays(2))
                .status(Status.WAITING)
                .booker(user2)
                .item(item)
                .build();
        entityManager.persist(booking);

        BookingDtoWithItemName bookingDtoWithItemName = service.updateBooking(user1.getId(), booking.getId(), true);

        TypedQuery<Booking> query = entityManager.createQuery("SELECT bk from Booking bk where bk.booker.id = :id", Booking.class);
        Booking bookingBase = query.setParameter("id", booking.getBooker().getId()).getSingleResult();

        assertEquals(bookingDtoWithItemName.getId(), bookingBase.getId());
        assertEquals(bookingDtoWithItemName.getStart(), bookingBase.getStart());
        assertEquals(bookingDtoWithItemName.getEnd(), bookingBase.getEnd());
        assertEquals(bookingDtoWithItemName.getStatus(), bookingBase.getStatus());
        assertEquals(bookingDtoWithItemName.getBooker().getId(), bookingBase.getBooker().getId());
        assertEquals(bookingDtoWithItemName.getItem().getId(), bookingBase.getItem().getId());
    }

    @Test
    void getBookingByIdTest() {

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusHours(1))
                .end(LocalDateTime.now().minusDays(2))
                .status(Status.APPROVED)
                .booker(user2)
                .item(item)
                .build();
        entityManager.persist(booking);

        BookingDtoWithItemName bookingDtoWithItemName = service.getBookingById(user1.getId(), booking.getId());
        TypedQuery<Booking> query = entityManager.createQuery("SELECT bk from Booking bk where bk.id = :id", Booking.class);
        Booking bookingBase = query.setParameter("id", booking.getId()).getSingleResult();

        assertEquals(bookingDtoWithItemName.getId(), bookingBase.getId());
        assertEquals(bookingDtoWithItemName.getStart(), bookingBase.getStart());
        assertEquals(bookingDtoWithItemName.getEnd(), bookingBase.getEnd());
        assertEquals(bookingDtoWithItemName.getStatus(), bookingBase.getStatus());
        assertEquals(bookingDtoWithItemName.getBooker().getId(), bookingBase.getBooker().getId());
        assertEquals(bookingDtoWithItemName.getItem().getId(), bookingBase.getItem().getId());

    }

    @Test
    void findAllByUser() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(user2)
                .item(item)
                .build();
        entityManager.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(Status.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        entityManager.persist(booking2);

        List<BookingDtoWithItemName> bookings = service.findAllByUser(user2.getId(), "ALL", 0, 20);
        TypedQuery<Booking> query = entityManager.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingBase.get(0).getItem().getId());

    }

    @Test
    void findAllByOwner() {
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(Status.WAITING)
                .booker(user2)
                .item(item)
                .build();
        entityManager.persist(booking);

        Booking booking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(5))
                .status(Status.REJECTED)
                .booker(user2)
                .item(item)
                .build();
        entityManager.persist(booking2);

        List<BookingDtoWithItemName> bookings = service.findAllByOwner(user1.getId(), "ALL", 0, 20);
        TypedQuery<Booking> query = entityManager.createQuery("SELECT bk from Booking bk " +
                "where bk.booker.id = :id", Booking.class);
        List<Booking> bookingBase = query
                .setParameter("id", user2.getId())
                .getResultList();

        assertEquals(2, bookings.size());
        assertEquals(bookings.get(0).getId(), bookingBase.get(1).getId());
        assertEquals(bookings.get(0).getStart(), bookingBase.get(1).getStart());
        assertEquals(bookings.get(0).getEnd(), bookingBase.get(1).getEnd());
        assertEquals(bookings.get(0).getStatus(), bookingBase.get(1).getStatus());
        assertEquals(bookings.get(0).getBooker().getId(), bookingBase.get(1).getBooker().getId());
        assertEquals(bookings.get(0).getItem().getId(), bookingBase.get(1).getItem().getId());
        assertEquals(bookings.get(1).getId(), bookingBase.get(0).getId());
        assertEquals(bookings.get(1).getStart(), bookingBase.get(0).getStart());
        assertEquals(bookings.get(1).getEnd(), bookingBase.get(0).getEnd());
        assertEquals(bookings.get(1).getStatus(), bookingBase.get(0).getStatus());
        assertEquals(bookings.get(1).getBooker().getId(), bookingBase.get(0).getBooker().getId());
        assertEquals(bookings.get(1).getItem().getId(), bookingBase.get(0).getItem().getId());
    }
}