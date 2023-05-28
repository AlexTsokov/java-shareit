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
    private String text;
    private Integer fromPage;
    private Integer sizePages;

    public static InfoFromRequest getInfoFromRequest(Long ownerId, Integer from, Integer size) {
        return InfoFromRequest.builder()
                .userId(ownerId)
                .fromPage(from)
                .sizePages(size)
                .build();
    }

    public static InfoFromRequest getInfoFromRequestWithText(String text, Integer from, Integer size) {
        return InfoFromRequest.builder()
                .text(text)
                .fromPage(from)
                .sizePages(size)
                .build();
    }
}
