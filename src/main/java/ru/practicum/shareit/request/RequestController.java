package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.item.model.InfoFromRequest;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/requests")
@AllArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    public RequestDto addRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody RequestDto request) {
        return requestService.addRequest(userId, request);
    }

    @GetMapping
    public List<RequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<RequestDto> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false, defaultValue = "10") Integer size) {
        return requestService.getRequests(InfoFromRequest.getInfoFromRequest(userId, from, size));
    }


    @GetMapping("/{requestId}")
    public RequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}
