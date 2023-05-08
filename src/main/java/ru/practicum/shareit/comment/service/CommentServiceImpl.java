package ru.practicum.shareit.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public CommentDto addComment(Long userId, Long itemId, Comment comment) {
        if (comment.getText().isBlank())
            throw new BookingNotFoundException("Комментарий не найден");
        comment.setItem(itemId);
        comment.setAuthor(userId);
        comment.setCreated(LocalDateTime.now());
        if (bookingRepository.findLastBookingByBookerId(comment.getItem(), comment.getAuthor()) == null) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        Comment savedComment = commentRepository.save(comment);
        CommentDto commentDto = CommentMapper.toCommentDto(savedComment);
        commentDto.setAuthorName(userRepository.findById(userId).get().getName());
        return commentDto;
    }
}
