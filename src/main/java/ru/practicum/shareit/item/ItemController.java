package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto create(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody ItemDto itemDto) {
        userService.checkUser(userId);
        return ItemMapper.toItemDto(itemService.createItem(userId, ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable Integer itemId,
                          @RequestBody ItemDto itemDto) {
        userService.checkUser(userId);
        itemService.checkOwner(userId, itemId);
        return ItemMapper.toItemDto(itemService.updateItem(userId, itemId, ItemMapper.toItem(itemDto)));
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@NotNull @PathVariable Integer id) {
        return ItemMapper.toItemDto(itemService.findItemById(id));
    }

    @GetMapping
    public List<ItemDto> getUserItemsList(@NotNull @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return ItemMapper.toDtoList(itemService.findItemsByUser(userId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@NotBlank @RequestParam String text) {
        return ItemMapper.toDtoList(itemService.searchItems(text));
    }
}
