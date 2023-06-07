package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.item.model.InfoFromRequest;

import java.util.List;

public interface RequestService {

    RequestDto addRequest(Long userId, RequestDto requestDto);

    List<RequestDto> getUserRequests(Long userId);

    List<RequestDto> getRequests(InfoFromRequest requestInfo);

    RequestDto getRequestById(Long userId, Long requestId);
}
