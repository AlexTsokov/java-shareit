package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Integer userId, Item item);

    Item updateItem(Integer userId, Integer itemId, Item item);

    Item findItemById(Integer id);

    List<Item> findItemsByUser(Integer userId);

    List<Item> searchItems(String text);

    boolean checkIfItemOwner(Integer userId, Integer itemId);
}