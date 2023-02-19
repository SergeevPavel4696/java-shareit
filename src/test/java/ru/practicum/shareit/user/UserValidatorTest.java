package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.validator.UserValidator;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserValidatorTest {
    private final User userWithoutName = User.builder().id(1).name(null).email("mail@mail.ya").build();
    private final User userWithoutEmail = new User(1, "Имя", null);
    private final User userWithIncorrectName1 = new User(1, "", "mail@mail.ya");
    private final User userWithIncorrectName2 = new User(1, "И мя", "mail@mail.ya");
    private final User userWithIncorrectEmail1 = new User(1, "Имя", "");
    private final User userWithIncorrectEmail2 = new User(1, "Имя", "mail.ya");
    private final UserDto userDtoWithIncorrectName1 = UserDto.builder().id(1).name("И мя").email("mail@mail.ya").build();
    private final UserDto userDtoWithIncorrectName2 = new UserDto(1, "", "mail@mail.ya");
    private final UserDto userDtoWithIncorrectEmail1 = new UserDto(1, "Имя", "");
    private final UserDto userDtoWithIncorrectEmail2 = new UserDto(1, "Имя", "mail.ya");

    @Test
    public void userValidateTest() {
        assertThrows(ValidationException.class, () -> UserValidator.validate(userWithoutName));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userWithoutEmail));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userWithIncorrectName1));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userWithIncorrectName2));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userWithIncorrectEmail1));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userWithIncorrectEmail2));
    }

    @Test
    public void userDtoValidateTest() {
        assertThrows(ValidationException.class, () -> UserValidator.validate(userDtoWithIncorrectName1));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userDtoWithIncorrectName2));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userDtoWithIncorrectEmail1));
        assertThrows(ValidationException.class, () -> UserValidator.validate(userDtoWithIncorrectEmail2));
    }
}
