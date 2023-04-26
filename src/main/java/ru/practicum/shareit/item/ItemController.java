package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Validated ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.createItem(userId, ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable Integer itemId,
                          @RequestBody ItemDto itemDto) {
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, ItemMapper.toItem(itemDto)));
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable Integer id) {
        return ItemMapper.toItemDto(itemService.findItemById(id));
    }

    @GetMapping
    public List<ItemDto> getUserItemsList(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ItemMapper.toDtoList(itemService.findItemsByUser(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return ItemMapper.toDtoList(itemService.searchItems(text));
    }
}
