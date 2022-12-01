package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {
    private Integer id;
    private String text;
    private Integer itemId;
    private Integer authorId;

}
