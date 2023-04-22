package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User changeUser(User user);

    List<User> getAllUsers();

    Optional<User> findUserById(Integer id);

    void deleteUser(Integer id);

    boolean checkUniqueOfEmail(Integer id, String email);

    Integer setUserId();

    Integer getUserId();
}