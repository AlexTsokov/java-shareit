package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.time.LocalDateTime;

@Builder
@Data
public class RequestDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDto> items;


}