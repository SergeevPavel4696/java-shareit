package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ItemWithBooking {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<Comment> comments;
}
