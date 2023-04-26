package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.dto.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Validated(Create.class) UserDto user) {
        return UserMapper.toUserDto(userService.createUser(UserMapper.toUser(user)));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable Integer id, @RequestBody @Validated(Update.class) UserDto user) {
        return UserMapper.toUserDto(userService.changeUser(id, UserMapper.toUser(user)));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return UserMapper.toDtoList(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        return UserMapper.toUserDto(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
