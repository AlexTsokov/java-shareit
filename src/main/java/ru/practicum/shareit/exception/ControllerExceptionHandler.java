package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorMessage resourceNotFoundException(EntityNotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.NOT_FOUND.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return message;
    }

    @ExceptionHandler(RequestNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage requestNotFoundException(RequestNotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return message;
    }

    @ExceptionHandler(BookingNotFoundException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage resourceNotFoundException(BookingNotFoundException ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return message;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage methodArgumentNotValidException(Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return message;
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorMessage missingRequestHeaderException(Exception ex, WebRequest request) {
        ErrorMessage message = new ErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        log.error(ex.getMessage(), ex);
        return message;
    }

    @ExceptionHandler(StateException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage stateException(Exception ex) {
        ErrorMessage message = new ErrorMessage(ex.getMessage());
        log.error(ex.getMessage(), ex);
        return message;
    }
}