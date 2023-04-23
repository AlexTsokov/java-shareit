package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public User createUser(User user) {
        if (!userStorage.checkUniqueOfEmail(user.getId(), user.getEmail())) {
            throw new EmailException("Почта уже существует");
        }
        userStorage.setUserId();
        user.setId(userStorage.getUserId());
        log.info("Пользователь добавлен");
        return userStorage.createUser(user);
    }

    @Override
    public User changeUser(Integer id, User user) {
        user.setId(id);
        if (user.getEmail() != null) {
            if (!userStorage.checkUniqueOfEmail(id, user.getEmail())) {
                throw new EmailException("Почта " + user.getEmail() + " уже существует");
            }
        }
        log.info("Пользователь обновлен");
        return userStorage.changeUser(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User findUserById(Integer id) {
        if (userStorage.findUserById(id).isEmpty()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }
        return userStorage.findUserById(id).get();
    }

    @Override
    public void deleteUser(Integer id) {
        log.info("Пользователь удален");
        userStorage.deleteUser(id);
    }

    @Override
    public boolean checkUserExist(Integer userId) {
        return (userId != null && userStorage.findUserById(userId).isPresent());
    }

}