package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.model.ItemDtoWithBooking;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ItemModelTest {
    private final ItemDto itemDto = new ItemDto(1, "Название", "Описание", true, null);
    private final ItemDtoWithBooking itemDtoWithBooking = ItemDtoWithBooking.builder().id(1).name("Название")
            .description("Описание").available(true).requestId(1).lastBooking(null).nextBooking(null).comments(null).build();
    private final CommentDto commentDto = new CommentDto(1, "Название", 1, "Автор");

    @Test
    public void itemDtoUpdateTest() {
        itemDto.setName("Обновлённое название");
        assertEquals("Обновлённое название", itemDto.getName());
    }

    @Test
    public void itemDtoWithBookingUpdateTest() {
        itemDtoWithBooking.setName("Обновлённое название");
        itemDtoWithBooking.setDescription("Обновлённое описание");
        itemDtoWithBooking.setAvailable(false);
        itemDtoWithBooking.setRequestId(2);
        itemDtoWithBooking.setComments(new ArrayList<>());
        assertEquals("Обновлённое название", itemDtoWithBooking.getName());
        assertEquals("Обновлённое описание", itemDtoWithBooking.getDescription());
        assertEquals(false, itemDtoWithBooking.getAvailable());
        assertEquals(2, itemDtoWithBooking.getRequestId());
        assertNull(itemDtoWithBooking.getLastBooking());
        assertNull(itemDtoWithBooking.getNextBooking());
        assertEquals(0, itemDtoWithBooking.getComments().size());
    }

    @Test
    public void commentDtoUpdateTest() {
        commentDto.setText("Обновлённый комментарий");
        assertEquals("Обновлённый комментарий", commentDto.getText());
    }
}
