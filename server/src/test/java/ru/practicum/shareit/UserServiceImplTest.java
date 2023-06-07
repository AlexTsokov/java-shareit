package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {

    private final UserService userService;
    private final UserRepository userRepository;
    private final EntityManager entityManager;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User();
        user.setName("name");
        user.setEmail("mail@email.ru");
        entityManager.persist(user);
    }

    @AfterEach
    void afterEach() {
        entityManager.createNativeQuery("truncate table users");
    }

    @Test
    void createUser() {
        User result = userService.createUser(user);

        assertEquals(result.getName(), user.getName());
        assertEquals(result.getEmail(), user.getEmail());
    }

    @Test
    void getAllUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setName("Name2");
        user2.setEmail("mail2@mail.ru");

        userService.createUser(user2);

        List<User> result = userService.getAllUsers();

        assertEquals(result.size(), 2);

    }

    @Test
    void findUserById() {
        User user = User
                .builder()
                .name("name")
                .email("mail@mail.ru")
                .build();

        userService.createUser(user);

        User result = userService.findUserById(user.getId());

        User userFromDB = userRepository.getReferenceById(user.getId());

        assertEquals(result.getId(), userFromDB.getId());
        assertEquals(result.getName(), userFromDB.getName());
        assertEquals(result.getEmail(), userFromDB.getEmail());
    }

    @Test
    void updateUser() {

        User user2 = User
                .builder()
                .name("newname")
                .email("newmail@mail.ru")
                .build();

        User result = userService.updateUser(user.getId(), user2);

        assertEquals(result.getName(), user2.getName());
        assertEquals(result.getEmail(), user2.getEmail());
    }

    @Test
    void deleteUser() {
        userService.createUser(user);
        userService.deleteUser(user.getId());
        List<User> result = userService.getAllUsers();
        assertEquals(result.size(), 0);
    }
}