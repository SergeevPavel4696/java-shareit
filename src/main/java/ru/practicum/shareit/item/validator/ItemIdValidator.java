package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.item.model.Item;

public class ItemIdValidator {
    public static void validate(Item item) {
        if (item == null) {
            throw new IncorrectId("Вещь по указанному id не существует.");
        }
    }
}
