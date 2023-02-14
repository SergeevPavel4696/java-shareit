package ru.practicum.shareit.request.validator;

import ru.practicum.shareit.exceptions.IncorrectId;

import java.util.List;

public class ItemRequestIdValidator {
    public static void validate(List<Integer> itemRequestsId, Integer itemRequestId) {
        if (!itemRequestsId.contains(itemRequestId)) {
            throw new IncorrectId("Вещь по указанному id не существует.");
        }
    }
}
