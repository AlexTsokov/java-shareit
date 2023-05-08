package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    // private final UserStorage userStorage;
    private final UserRepository userRepository;
   // private final UserValidator userValidator;


    @Override
    public User createUser(User user) {
//        if (!checkUniqueOfEmailOfNewUser(user.getEmail())) {
//            throw new EmailException("Почта уже существует");
//        }
        log.info("Пользователь добавлен");
        return userRepository.save(user);
    }


    @Override
    public User changeUser(Long id, User user) {
        User userForUpdate = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if (user.getEmail() != null) {
            if (!checkUniqueOfEmail(id, user.getEmail())) {
                throw new EmailException("Почта " + user.getEmail() + " уже существует");
            }
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userForUpdate.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userForUpdate.setEmail(user.getEmail());
        }
        log.info("Пользователь обновлен");
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
        log.info("Пользователь удален");
        userRepository.deleteById(id);
    }

    @Override
    public boolean checkUserExist(Long userId) {
        return (userId != null && userRepository.findById(userId).isPresent());
    }

    @Override
    public boolean checkUniqueOfEmail(Long id, String email) {
        for (User user : userRepository.findAll()) {
            if (user.getEmail().equals(email) && (long)user.getId() != id) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkUniqueOfEmailOfNewUser(String email) {
        for (User user : userRepository.findAll()) {
            if (user.getEmail().equals(email)) {
                return false;
            }
        }
        return true;
    }

}