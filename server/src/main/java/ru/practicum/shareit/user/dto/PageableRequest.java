package ru.practicum.shareit.user.dto;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@UtilityClass
public class PageableRequest {
    public Pageable getPageableRequest(Integer page, Integer size) {
        int fromPage = page / size;
        return PageRequest.of(fromPage, size);
    }
}
