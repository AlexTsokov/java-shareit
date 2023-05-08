package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Item changeItem(Item item, Long id);

    Optional<Item> findItemById(Long id);

    List<Item> findItemsByUser(Long userId);

    List<Item> searchItems(String text);

    long setItemId();

}

