package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

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
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Integer userId, @RequestBody @Valid ItemDto itemDto) {
        if (userService.checkUserExist(userId))
            return new ResponseEntity<>(ItemMapper.toItemDto(itemService.createItem(userId, ItemMapper.toItem(itemDto))), HttpStatus.OK);
        else return new ResponseEntity<>(itemDto, HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                          @PathVariable Integer itemId,
                                          @RequestBody ItemDto itemDto) {
        if (userService.checkUserExist(userId) && itemService.checkIfItemOwner(userId, itemId)) {
            return new ResponseEntity<>(ItemMapper.toItemDto(itemService.updateItem
                    (userId, itemId, ItemMapper.toItem(itemDto))), HttpStatus.OK);
        } else return new ResponseEntity<>(itemDto, HttpStatus.NOT_FOUND);
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
    public List<ItemDto> searchItems(@NotBlank @RequestParam String text) {
        return ItemMapper.toDtoList(itemService.searchItems(text));
    }
}
