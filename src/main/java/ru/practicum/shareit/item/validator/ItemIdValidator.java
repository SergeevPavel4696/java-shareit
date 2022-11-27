package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.exceptions.IncorrectId;

import java.util.List;

public class ItemIdValidator {
    public static void validate(List<Integer> itemsId, Integer itemId) {
        if (!itemsId.contains(itemId)) {
            throw new IncorrectId("Вещь по указанному id не существует.");
        }
    }
}
