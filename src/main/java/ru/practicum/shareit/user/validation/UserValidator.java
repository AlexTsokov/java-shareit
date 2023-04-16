package ru.practicum.shareit.user.validation;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

@Service
public class UserValidator {

    public boolean validate(User user) {
        return (user.getEmail() != null && user.getEmail().contains("@")) &&
                (user.getName() != null && !user.getName().isBlank());
    }
}
