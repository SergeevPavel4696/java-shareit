package ru.practicum.shareit.user.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class UserMapper {
    public static UserDto convert(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User convert(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
