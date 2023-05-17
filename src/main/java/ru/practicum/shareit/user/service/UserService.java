package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    List<User> getAllUsers();

    User changeUser(Long id, User user);

    User findUserById(Long id);

    void deleteUser(Long id);

    boolean checkUserExist(Long userId);

}