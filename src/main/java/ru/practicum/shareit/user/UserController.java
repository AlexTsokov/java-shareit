package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto user) {
        if (userService.validate(UserMapper.toUser(user))) {
            return new ResponseEntity<>(UserMapper.toUserDto
                    (userService.createUser(UserMapper.toUser(user))), HttpStatus.OK);
        } else return new ResponseEntity<>(user, HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{id}")
    public UserDto update(@NotNull @PathVariable Integer id, @RequestBody UserDto user) {
        return UserMapper.toUserDto(userService.changeUser(id, UserMapper.toUser(user)));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return UserMapper.toDtoList(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public UserDto getUser(@NotNull @PathVariable Integer id) {
        return UserMapper.toUserDto(userService.findUserById(id));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@NotNull @PathVariable Integer id) {
        userService.deleteUser(id);
    }
}
