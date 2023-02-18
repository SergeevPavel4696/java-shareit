package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    private final int id = 1;
    private final String name = "Имя";
    private final String email = "mail@mail.ya";

    private final User user = new User(id, name, email);
    private final UserDto userDto = new UserDto(id, name, email);

    @Test
    public void userToUserDtoTest() {
        UserDto userDto = UserMapper.convert(user);
        assertEquals(id, userDto.getId());
        assertEquals(name, userDto.getName());
        assertEquals(email, userDto.getEmail());
    }

    @Test
    public void userDtoToUserTest() {
        User user = UserMapper.convert(userDto);
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
    }
}
