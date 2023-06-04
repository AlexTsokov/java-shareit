package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.InfoFromRequest;

import java.util.List;

public interface ItemService {
    Item createItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    ItemDto findItemDtoById(Long userId, Long id);

    Item findItemById(Long id);

    List<ItemDto> getAllUserItems(InfoFromRequest infoFromRequest);

    List<Item> searchItems(InfoFromRequest infoFromRequest);

    boolean checkIfItemOwner(Long userId, Long itemId);

}