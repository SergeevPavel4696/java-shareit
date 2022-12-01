package ru.practicum.shareit.user.validator;

import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

public class UserValidator {
    public static void validate(User user) {
        if (user.getName() == null) {
            throw new ValidationException("Имя пользователя обязательное.");
        }
        if (user.getEmail() == null) {
            throw new ValidationException("Адрес электронной почты обязательный.");
        }
        if (user.getName().isEmpty() || user.getName().contains(" ")) {
            throw new ValidationException("Имя пользователя указано некорректно.");
        }
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Адрес электронной почты указан некорректно.");
        }
    }

    public static void validate(UserDto userDto) {
        if (userDto.getName() != null && (userDto.getName().isEmpty() || userDto.getName().contains(" "))) {
            throw new ValidationException("Имя пользователя указано некорректно.");
        }
        if (userDto.getEmail() != null && (userDto.getEmail().isEmpty() || !userDto.getEmail().contains("@"))) {
            throw new ValidationException("Адрес электронной почты указан некорректно.");
        }
    }
}
