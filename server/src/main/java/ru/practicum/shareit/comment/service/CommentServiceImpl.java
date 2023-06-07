package ru.practicum.shareit.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.dto.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Comment addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isBlank())
            throw new BookingNotFoundException("Комментарий не найден");
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(itemService.findItemById(itemId));
        comment.setAuthor(userService.findUserById(userId));
        comment.setCreated(LocalDateTime.now());
        if (bookingRepository.findLastBookingByBookerId(comment.getItem().getId(), comment.getAuthor().getId()) == null) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        commentRepository.save(comment);
        return comment;
    }
}
