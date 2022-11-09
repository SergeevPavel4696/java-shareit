package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.validator.UserIdValidator;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.convert(userDto);
        UserValidator.validate(user, getUsersName(), getUsersEmail());
        return UserMapper.convert(userStorage.create(user));
    }

    public UserDto update(int userId, UserDto userDto) {
        UserValidator.validate(userDto, getUsersName(), getUsersEmail());
        UserIdValidator.validate(getUsersId(), userId);
        User user = userStorage.get(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.convert(userStorage.update(user));
    }

    public UserDto delete(int userId) {
        UserIdValidator.validate(getUsersId(), userId);
        return UserMapper.convert(userStorage.delete(userId));
    }

    public UserDto get(int userId) {
        UserIdValidator.validate(getUsersId(), userId);
        return UserMapper.convert(userStorage.get(userId));
    }

    public List<UserDto> getAll() {
        List<UserDto> userDtos = new ArrayList<>();
        for (User user : userStorage.getAll()) {
            userDtos.add(UserMapper.convert(user));
        }
        return userDtos;
    }

    public List<Integer> getUsersId() {
        List<Integer> usersId = new ArrayList<>();
        for (User user : userStorage.getAll()) {
            usersId.add(user.getId());
        }
        return usersId;
    }

    public List<String> getUsersName() {
        List<String> usersName = new ArrayList<>();
        for (User user : userStorage.getAll()) {
            usersName.add(user.getName());
        }
        return usersName;
    }

    public List<String> getUsersEmail() {
        List<String> usersEmail = new ArrayList<>();
        for (User user : userStorage.getAll()) {
            usersEmail.add(user.getEmail());
        }
        return usersEmail;
    }
}
