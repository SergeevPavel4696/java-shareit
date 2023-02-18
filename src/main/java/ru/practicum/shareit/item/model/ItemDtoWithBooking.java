package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingEntry;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemDtoWithBooking {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer requestId;
    private BookingEntry lastBooking;
    private BookingEntry nextBooking;
    private List<CommentDto> comments;
}
