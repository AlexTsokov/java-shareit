package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

public interface CommentService {
    Comment addComment(Long userId, Long itemId, Comment comment);
}
