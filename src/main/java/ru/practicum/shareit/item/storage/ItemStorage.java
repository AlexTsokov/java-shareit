package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item);

    Item changeItem(Item item);

    Optional<Item> findItemById(Integer id);

    List<Item> findItemsByUser(Integer userId);

    List<Item> searchItems(String text);

    Integer setItemId();

    Integer getItemId();
}

