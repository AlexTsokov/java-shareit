package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Component
public class InMemoryItemStorage implements ItemStorage {

    private final Map<Integer, Item> items = new HashMap<>();
    private Integer itemId = 0;

    @Override
    public Integer setItemId() {
        itemId++;
        return itemId;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(setItemId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item changeItem(Item item, Integer id) {
        item.setId(id);
        Item itemForUpdate = items.get(item.getId());
        if (itemForUpdate == null) {
            throw new EntityNotFoundException("Вещь не найдена");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemForUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemForUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemForUpdate.setAvailable(item.getAvailable());
        }
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
            if (item.getOwnerId().equals(userId)) {
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
