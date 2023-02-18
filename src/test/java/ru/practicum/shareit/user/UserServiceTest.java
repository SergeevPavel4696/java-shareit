package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.Duplicate;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserJpaRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;
    @Mock
    private UserJpaRepository userJpaRepository;
    private User user1;
    private User user2;
    private User user1update;
    private UserDto userDto1;
    private UserDto userDto1update;
    private UserDto userDto2;

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1, "Имя1", "1@mail.ya");
        user1update = new User(1, "Имя1обн", "1обн@mail.ya");
        user2 = new User(2, "Имя2", "2@mail.ya");
        userDto1 = UserMapper.convert(user1);
        userDto1update = UserMapper.convert(user1update);
        userDto2 = UserMapper.convert(user2);
    }

    @Test
    public void createTest() {
        when(userJpaRepository.save(any())).thenReturn(user1);
        when(userJpaRepository.findById(1)).thenReturn(user1);
        userService.create(userDto1);
        assertEquals(user1.getId(), userService.getUser(1).getId());
        assertEquals(user1.getName(), userService.getUser(1).getName());
        assertEquals(user1.getEmail(), userService.getUser(1).getEmail());
    }

    @Test
    public void createWithDuplicateEmailTest() {
        when(userJpaRepository.save(any())).thenThrow(new Duplicate("Пользователь с таким email уже существует."));
        assertThrows(Duplicate.class, () -> userService.create(userDto1));
    }

    @Test
    public void updateTest() {
        when(userJpaRepository.save(any())).thenReturn(user1);
        userService.create(userDto1);
        when(userJpaRepository.findById(1)).thenReturn(user1);
        UserDto updatedUser = userService.update(1, userDto1update);
        assertEquals(user1update.getId(), userService.getUser(updatedUser.getId()).getId());
        assertEquals(user1update.getName(), userService.getUser(updatedUser.getId()).getName());
        assertEquals(user1update.getEmail(), userService.getUser(updatedUser.getId()).getEmail());
    }

    @Test
    public void updateNotFoundTest() {
        when(userJpaRepository.findById(Mockito.anyInt())).thenThrow(new IncorrectId("Пользователь по указанному id не существует."));
        assertThrows(IncorrectId.class, () -> userService.update(1, userDto1update));
    }

    @Test
    public void updateWithDuplicateEmailTest() {
        when(userJpaRepository.save(any())).thenReturn(user1);
        userService.create(userDto1);
        when(userJpaRepository.findById(Mockito.anyInt())).thenReturn(user1);
        when(userJpaRepository.save(any())).thenThrow(new Duplicate("Пользователь с таким email уже существует."));
        userDto1update.setEmail("1@mail.ya");
        assertThrows(Duplicate.class, () -> userService.update(1, userDto1update));
    }

    @Test
    public void deleteTest() {
        when(userJpaRepository.save(any())).thenReturn(user1);
        userService.create(userDto1);
        when(userJpaRepository.findById(1)).thenReturn(user1);
        UserDto userDto = userService.delete(1);
        assertEquals(userDto.getId(), user1.getId());
        assertEquals(userDto.getName(), user1.getName());
        assertEquals(userDto.getEmail(), user1.getEmail());
        when(userJpaRepository.findById(Mockito.anyInt())).thenReturn(null);
        assertThrows(IncorrectId.class, () -> userService.get(1));
    }

    @Test
    public void deleteNotFoundTest() {
        when(userJpaRepository.findById(Mockito.anyInt())).thenReturn(null);
        assertThrows(IncorrectId.class, () -> userService.delete(1));
    }

    @Test
    public void getUserTest() {
        when(userJpaRepository.findById(1)).thenReturn(user1);
        UserDto userDto = userService.get(1);
        assertEquals(1, userDto.getId());
        assertEquals("Имя1", userDto.getName());
        assertEquals("1@mail.ya", userDto.getEmail());
    }

    @Test
    public void getUserNotFoundTest() {
        when(userJpaRepository.findById(Mockito.anyInt())).thenReturn(null);
        assertThrows(IncorrectId.class, () -> userService.get(1));
    }

    @Test
    public void getUserDtoNotFoundTest() {
        when(userJpaRepository.findById(Mockito.anyInt())).thenReturn(null);
        assertThrows(IncorrectId.class, () -> userService.get(1));
    }

    @Test
    public void getAllTest() {
        when(userJpaRepository.save(any())).thenReturn(user1);
        userService.create(userDto1);
        when(userJpaRepository.save(any())).thenReturn(user2);
        userService.create(userDto2);
        when(userJpaRepository.findAll()).thenReturn(List.of(user1, user2));
        List<UserDto> users = userService.getAll();
        assertEquals(users.get(0), userDto1);
        assertEquals(users.get(1), userDto2);
        assertEquals(2, users.size());
    }
}
