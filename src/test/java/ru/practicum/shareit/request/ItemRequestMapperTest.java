package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemRequestMapperTest {

    private final int userId = 1;
    private final String userName = "Имя";
    private final String email = "mail@mail.ya";
    private final User requester = new User(userId, userName, email);

    private final int requestId = 2;
    private final String description = "Описание";
    private final LocalDateTime created = LocalDateTime.of(2020, 10, 15, 12, 40, 35);
    private final ItemRequest itemRequest = new ItemRequest(requestId, description, requester, created);
    private final ItemRequestDto itemRequestDto = new ItemRequestDto(requestId, description);

    private final List<ItemDto> items = List.of(
            new ItemDto(1, "Название 1", "Описание 1", true, null),
            new ItemDto(2, "Название 2", "Описание 2", false, 1),
            new ItemDto(3, "Название 3", "Описание 3", true, 2)
    );

    @Test
    public void ItemRequestToItemRequestDtoTest() {
        ItemRequestDto itemRequestDto = ItemRequestMapper.convert(itemRequest);
        assertEquals(requestId, itemRequestDto.getId());
        assertEquals(description, itemRequestDto.getDescription());
    }

    @Test
    public void ItemRequestDtoToItemRequestTest() {
        ItemRequest itemRequest = ItemRequestMapper.convert(itemRequestDto, requester, created);
        assertEquals(requestId, itemRequest.getId());
        assertEquals(description, itemRequest.getDescription());
        assertEquals(requester.getId(), itemRequest.getRequester().getId());
        assertEquals(created, itemRequest.getCreated());
    }

    @Test
    public void ItemRequestToItemRequestDtoResponseTest() {
        ItemRequestDtoResponse itemRequestDtoResponse = ItemRequestMapper.convertToResponse(itemRequest);
        assertEquals(requestId, itemRequestDtoResponse.getId());
        assertEquals(description, itemRequestDtoResponse.getDescription());
        assertEquals(created, itemRequestDtoResponse.getCreated());
    }

    @Test
    public void ItemRequestToItemRequestDtoWithItemsTest() {
        ItemRequestDtoWithItems itemRequestDtoWithItems = ItemRequestMapper.convert(itemRequest, new ArrayList<>(items));
        assertEquals(requestId, itemRequestDtoWithItems.getId());
        assertEquals(description, itemRequestDtoWithItems.getDescription());
        assertEquals(created, itemRequestDtoWithItems.getCreated());
        assertEquals(items, itemRequestDtoWithItems.getItems());
    }
}
