package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;

    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Item createItem(Integer userId, Item item) {
        if (!userService.checkUserExist(userId))
            throw new EntityNotFoundException("Не найден");
        itemStorage.setItemId();
        item.setId(itemStorage.getItemId());
        item.setOwnerId(userId);
        log.info("Вещь добавлена");
        return itemStorage.createItem(item);
    }

    @Override
    public Item updateItem(Integer userId, Integer itemId, Item item) {
        if (userService.checkUserExist(userId) && checkIfItemOwner(userId, itemId)) {
            item.setId(itemId);
            item.setOwnerId(userId);
            log.info("Вещь обновлена");
            return itemStorage.changeItem(item);
        } else throw new EntityNotFoundException("Пользователь данной вещи не найден");
    }

    @Override
    public Item findItemById(Integer id) {
        if (itemStorage.findItemById(id).isEmpty()) {
            throw new EntityNotFoundException("Не найдено вещи с таким идентификатором");
        }
        return itemStorage.findItemById(id).get();
    }

    @Override
    public List<Item> findItemsByUser(Integer userId) {
        return itemStorage.findItemsByUser(userId);
    }

    @Override
    public List<Item> searchItems(String text) {
        if (!text.isEmpty())
            return itemStorage.searchItems(text);
        else return Collections.emptyList();
    }

    public boolean checkIfItemOwner(Integer userId, Integer itemId) {
        Item item = findItemById(itemId);
        return (int) item.getOwnerId() == userId;
    }
}
