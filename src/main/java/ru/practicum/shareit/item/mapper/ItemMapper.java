package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemWithBooking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public class ItemMapper {
    public static ItemDto convert(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item convert(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), owner);
    }

    public static ItemWithBooking convert(Item item, BookingEntry lastBooking, BookingEntry nextBooking, List<CommentDto> comments) {
        return new ItemWithBooking(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), lastBooking, nextBooking, comments);
    }
}
