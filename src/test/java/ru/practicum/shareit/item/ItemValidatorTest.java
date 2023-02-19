package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.validator.ItemValidator;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemValidatorTest {
    private final User owner = new User(1, "Имя", "mail@mail.ya");
    private final Item itemWithoutName = new Item(1, null, "Описание", true, owner, null);
    private final Item itemWithoutDescription = new Item(1, "Название", null, true, owner, null);
    private final Item itemWithoutAvailable = new Item(1, "Название", "Описание", null, owner, null);
    private final Item itemWithoutIncorrectName = new Item(1, "", "Описание", true, owner, null);
    private final Item itemWithoutIncorrectDescription = new Item(1, "Название", "", true, owner, null);
    private final ItemDto itemWithIncorrectName = ItemDto.builder().id(1).name("").description("Описание").available(true).requestId(null).build();
    private final ItemDto itemWithIncorrectDescription = new ItemDto(1, "Название", "", true, null);

    @Test
    public void itemValidateTest() {
        assertThrows(ValidationException.class, () -> ItemValidator.validate(itemWithoutName));
        assertThrows(ValidationException.class, () -> ItemValidator.validate(itemWithoutDescription));
        assertThrows(ValidationException.class, () -> ItemValidator.validate(itemWithoutAvailable));
        assertThrows(ValidationException.class, () -> ItemValidator.validate(itemWithoutIncorrectName));
        assertThrows(ValidationException.class, () -> ItemValidator.validate(itemWithoutIncorrectDescription));
    }

    @Test
    public void itemDtoValidateTest() {
        assertThrows(ValidationException.class, () -> ItemValidator.validate(itemWithIncorrectName));
        assertThrows(ValidationException.class, () -> ItemValidator.validate(itemWithIncorrectDescription));
    }
}
