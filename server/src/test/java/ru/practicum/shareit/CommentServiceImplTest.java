package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceImplTest {

    private User user;
    private Item item;
    private final CommentService service;
    private final EntityManager entityManager;
    private final CommentRepository commentRepository;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("name");
        user.setEmail("mail@email.ru");
        entityManager.persist(user);

        item = new Item();
        item.setName("itemname");
        item.setDescription("item description");
        item.setOwner(user);
        item.setAvailable(true);
        entityManager.persist(item);

        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5))
                .booker(user)
                .status(Status.APPROVED)
                .item(item)
                .build();
        entityManager.persist(booking);
    }

    @AfterEach
    void afterEach() {
        entityManager.createNativeQuery("truncate table comments");
    }

    @Test
    void addCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .authorName("name")
                .created(LocalDateTime.now())
                .text("comment text")
                .build();

        Comment comment = service.addComment(user.getId(), item.getId(), commentDto);

        Comment commentFromDB = commentRepository.getReferenceById(comment.getId());

        assertEquals(comment.getId(), commentFromDB.getId());
        assertEquals(comment.getAuthor(), commentFromDB.getAuthor());
        assertEquals(comment.getText(), commentFromDB.getText());
    }
}