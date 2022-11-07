package ru.practicum.shareit.user.validator;

import ru.practicum.shareit.exceptions.IncorrectId;

import java.util.List;

public class UserIdValidator {
    public static void validate(List<Integer> usersId, Integer userId) {
        if (!usersId.contains(userId)) {
            throw new IncorrectId("Пользователь по указанному id не существует.");
        }
    }
}
