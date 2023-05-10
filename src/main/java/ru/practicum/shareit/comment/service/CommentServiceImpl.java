package ru.practicum.shareit.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Comment addComment(Long userId, Long itemId, Comment comment) {
        if (comment.getText().isBlank())
            throw new BookingNotFoundException("Комментарий не найден");
        comment.setItem(itemId);
        comment.setAuthor(userId);
        comment.setCreated(LocalDateTime.now());
        if (bookingRepository.findLastBookingByBookerId(comment.getItem(), comment.getAuthor()) == null) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        commentRepository.save(comment);
        return comment;
    }
}
