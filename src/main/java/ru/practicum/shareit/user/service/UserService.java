package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user);

    List<User> getAllUsers();

    User changeUser(Integer id, User user);

    User findUserById(Integer id);

    void deleteUser(Integer id);

    boolean checkUserExist(Integer userId);

    boolean validate(User user);
}