package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @Valid @RequestBody ItemDto item) {
        log.info("Принят запрос на добавление предмета от пользователя с айди: {}", ownerId);
        return itemClient.addItem(ownerId, item);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItem(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        log.info("Принят запрос на просмотр вещи от пользователя с айди: {}", userId);
        return itemClient.findItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Принят запрос на просмотр всех вещей пользователя с айди: {}", ownerId);
        return itemClient.findAllUserItems(ownerId, from, size);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestBody ItemDto itemDto, @PathVariable Long itemId) {
        log.info("Принят запрос на обновление предмета от пользователя с айди: {}", ownerId);
        return itemClient.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam String text,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(defaultValue = "10") Integer size) {

        if (text.isBlank()) return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        log.info("Принят запрос на поиск предмета");
        return itemClient.findItemsByText(text, from, size, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                             @RequestBody CommentDto commentDto, @PathVariable Long itemId) {
        log.info("Принят запрос на добавление комментария");
        return itemClient.addComment(ownerId, commentDto, itemId);
    }
}
