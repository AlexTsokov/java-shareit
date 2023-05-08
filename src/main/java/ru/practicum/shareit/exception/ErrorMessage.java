package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private int statusCode;
    private Date timestamp;
    private String error;
    private String description;

    public ErrorMessage(String error){
        this.error = error;
    }

}
