package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    public static ItemRequestDto convert(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription());
    }

    public static ItemRequestDtoResponse convertToResponse(ItemRequest itemRequest) {
        return new ItemRequestDtoResponse(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated());
    }

    public static ItemRequest convert(ItemRequestDto itemRequestDto, User requester, LocalDateTime created) {
        return new ItemRequest(itemRequestDto.getId(), itemRequestDto.getDescription(), requester, created);
    }

    public static ItemRequestDtoWithItems convert(ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestDtoWithItems(itemRequest.getId(), itemRequest.getDescription(), itemRequest.getCreated(), items);
    }
}
