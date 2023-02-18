package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    private final int userId = 1;
    private final String userName = "Имя";
    private final String email = "mail@mail.ya";
    private final User owner = new User(userId, userName, email);

    private final int itemId = 1;
    private final String itemName = "Название";
    private final String description = "Описание";
    private final boolean available = true;
    private final int itemRequestId = 3;
    private final Item item = new Item(itemId, itemName, description, available, owner, itemRequestId);
    private final ItemDto itemDto = new ItemDto(itemId, itemName, description, available, itemRequestId);

    @Test
    public void itemToItemDtoTest() {
        ItemDto itemDto = ItemMapper.convert(item);
        assertEquals(itemId, itemDto.getId());
        assertEquals(itemName, itemDto.getName());
        assertEquals(description, itemDto.getDescription());
        assertEquals(available, itemDto.getAvailable());
        assertEquals(itemRequestId, itemDto.getRequestId());
    }

    @Test
    public void itemDtoToItemTest() {
        Item item = ItemMapper.convert(itemDto, owner);
        assertEquals(itemId, item.getId());
        assertEquals(itemName, item.getName());
        assertEquals(description, item.getDescription());
        assertEquals(available, item.getAvailable());
        assertEquals(owner.getId(), item.getOwner().getId());
        assertEquals(itemRequestId, item.getRequestId());
    }
}
