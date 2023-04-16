package ru.practicum.shareit.item.validation;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;

@Service
public class ItemValidator {

    public boolean validate(Item item) {
        return ((item.getName() != null && !item.getName().isBlank()) &&
                (item.getDescription() != null && !item.getDescription().isBlank()) &&
                item.getAvailable() != null);
    }
}