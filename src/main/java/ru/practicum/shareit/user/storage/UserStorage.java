package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User changeUser(Long id, User user);

    List<User> getAllUsers();

    Optional<User> findUserById(Long id);

    void deleteUser(Long id);

//    boolean checkUniqueOfEmail(Integer id, String email);
//
//    boolean checkUniqueOfEmailOfNewUser(String email);

    Long setUserId();
}