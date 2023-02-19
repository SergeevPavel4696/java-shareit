package ru.practicum.shareit.request.validator;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;

public class ItemRequestValidator {
    public static void validate(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null) {
            throw new ValidationException("Описание запроса обязательное.");
        }
        if (itemRequest.getRequester() == null) {
            throw new ValidationException("Автор запроса обязателен.");
        }
        if (itemRequest.getCreated() == null) {
            throw new ValidationException("Время создания запроса вещи обязательное.");
        }
    }

    public static void validate(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() != null && itemRequestDto.getDescription().isEmpty()) {
            throw new ValidationException("Описание запроса указано некорректно.");
        }
    }
}
