package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage {

    Map<Integer, Item> items = new HashMap<>();

    @Override
    public Item createItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item changeItem(Item item) {
        Item itemForUpdate = items.get(item.getId());
        if (itemForUpdate == null) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (item.getName() != null) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
        items.put(itemForUpdate.getId(), itemForUpdate);
        return itemForUpdate;
    }

    @Override
    public Optional<Item> findItemById(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findItemsByUser(Integer userId) {
        List<Item> userItemsList = new ArrayList<>();
        for (Item item : items.values()) {
            if ((int)item.getOwnerId() == userId) {
                userItemsList.add(item);
            }
        }
        return userItemsList;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> searchResultList = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getAvailable()) {
                if (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) {
                    searchResultList.add(item);
                }
            }
        }
        return searchResultList;
    }
}
