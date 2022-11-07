package ru.practicum.shareit.user.validator;

import ru.practicum.shareit.exceptions.Duplicate;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.util.List;

public class UserValidator {
    public static void validate(User user, List<String> usersName, List<String> usersEmail) {
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
        if (usersName.contains(user.getName())) {
            throw new Duplicate("Пользователь с указанным именем уже существует.");
        }
        if (usersEmail.contains(user.getEmail())) {
            throw new Duplicate("Пользователь с указанным email уже существует.");
        }
    }

    public static void validate(UserDto userDto, List<String> usersName, List<String> usersEmail) {
        if (userDto.getName() != null) {
            if (userDto.getName().isEmpty() || userDto.getName().contains(" ")) {
                throw new ValidationException("Имя пользователя указано некорректно.");
            }
            if (usersName.contains(userDto.getName())) {
                throw new Duplicate("Пользователь с указанным именем уже существует.");
            }
        }
        if (userDto.getEmail() != null) {
            if (userDto.getEmail().isEmpty() || !userDto.getEmail().contains("@")) {
                throw new ValidationException("Адрес электронной почты указан некорректно.");
            }
            if (usersEmail.contains(userDto.getEmail())) {
                throw new Duplicate("Пользователь с указанным email уже существует.");
            }
        }
    }
}
