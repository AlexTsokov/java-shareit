package ru.practicum.shareit.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@NotNull @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @NotNull @PathVariable long itemId,
                                 @Valid @RequestBody Comment comment) {
        return commentService.addComment(userId, itemId, comment);
    }
}
