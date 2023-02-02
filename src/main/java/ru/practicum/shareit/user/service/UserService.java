package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.Duplicate;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.validator.UserIdValidator;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserJpaRepository userJpaRepository;

    public UserDto create(UserDto userDto) {
        UserValidator.validate(userDto);
        User user = UserMapper.convert(userDto);
        UserValidator.validate(user);
        try {
            return UserMapper.convert(userJpaRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new Duplicate("Пользователь с указанными данными уже существует.");
        }
    }

    public UserDto update(int userId, UserDto userDto) {
        UserValidator.validate(userDto);
        UserIdValidator.validate(getUsersId(), userId);
        User user = userJpaRepository.findById(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        try {
            user = userJpaRepository.save(user);
            return UserMapper.convert(user);
        } catch (DataIntegrityViolationException e) {
            throw new Duplicate("Пользователь с указанными данными уже существует.");
        }
    }

    public UserDto delete(int userId) {
        UserIdValidator.validate(getUsersId(), userId);
        User user = getUser(userId);
        userJpaRepository.delete(user);
        return UserMapper.convert(user);
    }

    public UserDto get(int userId) {
        UserIdValidator.validate(getUsersId(), userId);
        User user = userJpaRepository.findById(userId);
        return UserMapper.convert(user);
    }

    public User getUser(int userId) {
        UserIdValidator.validate(getUsersId(), userId);
        return userJpaRepository.findById(userId);
    }

    public List<UserDto> getAll() {
        return userJpaRepository.findAll().stream().map(UserMapper::convert).collect(Collectors.toList());
    }

    public List<Integer> getUsersId() {
        return userJpaRepository.findAll().stream().map(User::getId).collect(Collectors.toList());
    }
}
