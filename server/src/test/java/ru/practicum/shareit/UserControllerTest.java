package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void createUserTest() throws Exception {
        User user = User
                .builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();

        when(userService.createUser(any())).thenReturn(user);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void getUserTest() throws Exception {
        User user = User
                .builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();

        when(userService.findUserById(any())).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void getAllUsers() throws Exception {
        User user = User
                .builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();

        User user2 = User
                .builder()
                .id(2L)
                .name("name2")
                .email("email2@mail.ru")
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(user, user2));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(user, user2))));
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void updateUserTest() throws Exception {

        UserDto user = UserDto
                .builder()
                .id(1L)
                .name("name")
                .email("email@mail.ru")
                .build();

        UserDto user2 = UserDto
                .builder()
                .id(1L)
                .name("name2")
                .email("email2@mail.ru")
                .build();

        when(userService.updateUser(1L, UserMapper.toUser(user2))).thenReturn(UserMapper.toUser(user2));

        mockMvc.perform(patch("/users/1").content(mapper.writeValueAsString(user2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user2.getId()))
                .andExpect(jsonPath("$.name").value(user2.getName()))
                .andExpect(jsonPath("$.email").value(user2.getEmail()));
    }

}
