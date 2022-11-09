package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDto;

public class ItemMapper {
    public static ItemDto convert(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item convert(ItemDto itemDto, Integer ownerId) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable(), ownerId);
    }
}
