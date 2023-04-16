package ru.practicum.shareit.user.exception;
public class UserNotFoundException extends NullPointerException {
    public UserNotFoundException(String s) {
        super(s);
    }
}
