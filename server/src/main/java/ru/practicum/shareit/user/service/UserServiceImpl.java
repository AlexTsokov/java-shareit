package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        log.info("Пользователь {} добавлен", user.getName());
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User user) {
        User userForUpdate = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (user.getName() != null && !user.getName().isBlank()) {
            userForUpdate.setName(user.getName());
            log.info("Пользователь {} обновлен", user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userForUpdate.setEmail(user.getEmail());
        }
        return userRepository.save(userForUpdate);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Пользователь с id {} удален", id);
        userRepository.deleteById(id);
    }

    @Override
    public boolean checkUserExist(Long userId) {
        return (userId != null && userRepository.findById(userId).isPresent());
    }

}