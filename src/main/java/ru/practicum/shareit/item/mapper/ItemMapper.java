package ru.practicum.shareit.item.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemDtoWithBooking;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class ItemMapper {
    public static ItemDto convert(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequestId());
    }

    public static Item convert(ItemDto itemDto, User owner) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(),
                itemDto.getAvailable(), owner, itemDto.getRequestId());
    }

    public static ItemDtoWithBooking convert(Item item, BookingEntry lastBooking, BookingEntry nextBooking, List<CommentDto> comments) {
        return new ItemDtoWithBooking(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), item.getRequestId(), lastBooking, nextBooking, comments);
    }
}
