package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.item.model.*;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto convert(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item convert(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner);
    }

    public static ItemWithBooking convert(Item item, BookingDto lastBooking, BookingDto nextBooking, List<Comment> comments) {
        return new ItemWithBooking(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), lastBooking, nextBooking, comments);
    }
}
