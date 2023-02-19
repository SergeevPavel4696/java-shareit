package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDtoResponse {
    private Integer id;
    private String description;
    private LocalDateTime created;
}
