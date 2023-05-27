package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class InfoFromRequest {
    private Long userId;
    private Integer fromPage;
    private Integer sizePages;

    public static InfoFromRequest getInfoFromRequest(Long ownerId, Integer from, Integer size) {
        return InfoFromRequest.builder()
                .userId(ownerId)
                .fromPage(from)
                .sizePages(size)
                .build();
    }
}
