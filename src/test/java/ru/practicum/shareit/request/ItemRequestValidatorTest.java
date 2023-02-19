package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.validator.ItemRequestValidator;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ItemRequestValidatorTest {
    private final User requester = new User(1, "Имя", "mail@mail.ya");
    private final ItemRequest itemRequestWithoutDescription = new ItemRequest(1, null, requester, LocalDateTime.now());
    private final ItemRequest itemRequestWithoutRequester = new ItemRequest(1, "Описание", null, LocalDateTime.now());
    private final ItemRequest itemRequestWithoutCreatedDate = new ItemRequest(1, "Описание", requester, null);
    private final ItemRequestDto itemRequestDtoWithIncorrectDescription = new ItemRequestDto(1, "");

    @Test
    public void itemRequestValidateTest() {
        assertThrows(ValidationException.class, () -> ItemRequestValidator.validate(itemRequestWithoutDescription));
        assertThrows(ValidationException.class, () -> ItemRequestValidator.validate(itemRequestWithoutRequester));
        assertThrows(ValidationException.class, () -> ItemRequestValidator.validate(itemRequestWithoutCreatedDate));
    }

    @Test
    public void itemRequestDtoValidateTest() {
        assertThrows(ValidationException.class, () -> ItemRequestValidator.validate(itemRequestDtoWithIncorrectDescription));
    }
}
