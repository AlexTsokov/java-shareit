package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User changeUser(User user) {
        User userForUpdate = users.get(user.getId());
        if (userForUpdate == null) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (user.getName() != null) {
            userForUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userForUpdate.setEmail(user.getEmail());
        }
        users.put(userForUpdate.getId(), userForUpdate);
        return userForUpdate;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUser(Integer id) {
        users.remove(id);
    }

    @Override
    public boolean checkUniqueOfEmail(Integer id, String email) {
        for (User user : users.values()) {
            if (user.getEmail().equals(email) && (int)user.getId() != id) {
                return false;
            }
        }
        return true;
    }
}