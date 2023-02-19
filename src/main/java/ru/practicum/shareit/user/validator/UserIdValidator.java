package ru.practicum.shareit.user.validator;

import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

public class UserIdValidator {
    public static void validate(UserDto userDto) {
        if (userDto == null) {
            throw new IncorrectId("Пользователь по указанному id не существует.");
        }
    }

    public static void validate(User user) {
        if (user == null) {
            throw new IncorrectId("Пользователь по указанному id не существует.");
        }
    }
}
