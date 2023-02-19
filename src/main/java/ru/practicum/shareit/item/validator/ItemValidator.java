package ru.practicum.shareit.item.validator;

import ru.practicum.shareit.exceptions.ServerError;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

public class ItemValidator {
    public static void validate(Item item) {
        if (item.getName() == null) {
            throw new ValidationException("Название вещи обязательное.");
        }
        if (item.getDescription() == null) {
            throw new ValidationException("Описание вещи обязательное.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Статус аренды вещи обязателен.");
        }
        if (item.getName().isEmpty()) {
            throw new ValidationException("Название вещи указано некорректно.");
        }
        if (item.getDescription().isEmpty()) {
            throw new ValidationException("Описание вещи указано некорректно.");
        }
    }

    public static void validate(ItemDto itemDto) {
        if (itemDto.getName() != null && itemDto.getName().isEmpty()) {
            throw new ValidationException("Название вещи указано некорректно.");
        }
        if (itemDto.getDescription() != null && itemDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание вещи указано некорректно.");
        }
    }
}
