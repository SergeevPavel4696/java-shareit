package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

public class UserMapper {
    public static UserDto convert(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User convert(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
