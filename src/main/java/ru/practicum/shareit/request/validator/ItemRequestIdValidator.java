package ru.practicum.shareit.request.validator;

import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.request.model.ItemRequest;

public class ItemRequestIdValidator {
    public static void validate(ItemRequest itemRequest) {
        if (itemRequest == null) {
            throw new IncorrectId("Запрос по указанному id не существует.");
        }
    }
}
