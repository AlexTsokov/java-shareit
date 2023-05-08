package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public Long setUserId() {
        userId++;
        return userId;
    }

    @Override
    public User createUser(User user) {
        user.setId(setUserId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User changeUser(Long id, User user) {
        user.setId(id);
        User userForUpdate = users.get(user.getId());
        if (userForUpdate == null) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userForUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userForUpdate.setEmail(user.getEmail());
        }
        return userForUpdate;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }


}