package ru.practicum.shareit.exception;

public class MethodArgumentNotValidException extends RuntimeException {
    public MethodArgumentNotValidException(String s) {
        super(s);
    }
}
