package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.item.model.InfoFromRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.PageableRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestRepository requestRepository;

    @Override
    public RequestDto addRequest(Long userId, RequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new RequestNotFoundException("Описание не может быть пустым");
        }
        Request request = RequestMapper.mapFromRequestDto(requestDto);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        Request inBaseRequest = requestRepository.save(request);
        return RequestMapper.mapFromRequest(inBaseRequest);
    }

    @Override
    public List<RequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        List<Request> requests = requestRepository.getRequestsByRequesterId(userId);
        return settingRequestDtoList(requests);
    }

    @Override
    public List<RequestDto> getRequests(InfoFromRequest requestInfo) {
        Long userId = requestInfo.getUserId();
        Integer size = requestInfo.getSizePages();
        Integer from = requestInfo.getFromPage();
        List<RequestDto> requestsDto;
        Pageable pageable = PageableRequest.getPageableRequest(from, size);
        if ((size < 0 || from < 0)) {
            throw new RequestNotFoundException("Неверный формат запросов");
        }
        List<Request> requests = requestRepository.findByRequester_IdNot(userId, pageable);
        requestsDto = settingRequestDtoList(requests);
        return requestsDto;
    }

    @Override
    public RequestDto getRequestById(Long userId, Long requestId) {
        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);
        Request request = requestRepository.findById(requestId).orElseThrow(EntityNotFoundException::new);
        RequestDto requestDto = RequestMapper.mapFromRequest(request);
        addItems(requestDto);
        return requestDto;
    }


    private void addItems(RequestDto requestDto) {
        List<ItemDto> items = itemRepository.getItemsByRequestId(requestDto.getId()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        requestDto.setItems(items);
    }

    private List<RequestDto> settingRequestDtoList(List<Request> requests) {
        Map<Long, List<ItemDto>> items = itemRepository.getItemsByRequestIdIn(
                        requests.stream().map(Request::getId).collect(Collectors.toList())).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId, Collectors.toList()));
        List<RequestDto> requestsDto = requests.stream()
                .map(RequestMapper::mapFromRequest)
                .collect(Collectors.toList());
        requestsDto.forEach(r -> r.setItems(items.getOrDefault(r.getId(), Collections.emptyList())));
        return requestsDto;
    }
}
