package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceTest {
    @InjectMocks
    private ItemService itemService;
    @Mock
    private ItemJpaRepository itemJpaRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingJpaRepository bookingJpaRepository;
    @Mock
    private CommentJpaRepository commentJpaRepository;
    private User owner;
    private UserDto ownerDto;
    private User author;
    private UserDto authorDto;
    private Item item;
    private ItemDto itemDto;
    private Item updateItem;
    private ItemDto updateItemDto;
    private Booking booking;
    private Comment comment;
    private final IncorrectId notFoundException = new IncorrectId("Объект не найден");
    LocalDateTime start = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
    LocalDateTime end = LocalDateTime.of(2020, 1, 2, 0, 0, 0);

    @BeforeEach
    public void beforeEach() {
        owner = new User(1, "Владелец", "owner@mail.ya");
        ownerDto = UserMapper.convert(owner);
        author = new User(1, "Автор", "author@mail.ya");
        authorDto = UserMapper.convert(author);
        item = Item.builder().id(1).name("Название").description("Описание").available(true).owner(owner).requestId(null).build();
        itemDto = ItemMapper.convert(item);
        updateItem = new Item(1, "Обновлённое название", "Обновлённое описание", true, owner, null);
        updateItemDto = ItemMapper.convert(updateItem);
        booking = new Booking(1, start, end, item, owner, APPROVED);
        comment = new Comment(1, "Комментарий", item, author);
    }

    @Test
    public void createTest() {
        when(userService.get(1)).thenReturn(ownerDto);
        when(itemJpaRepository.save(any())).thenReturn(item);
        itemService.create(itemDto, owner.getId());
        when(itemJpaRepository.findById(1)).thenReturn(item);
        assertEquals(item.getId(), itemService.getItem(1).getId());
        assertEquals(item.getName(), itemService.getItem(1).getName());
        assertEquals(item.getDescription(), itemService.getItem(1).getDescription());
        assertEquals(item.getAvailable(), itemService.getItem(1).getAvailable());
        assertEquals(item.getRequestId(), itemService.getItem(1).getRequestId());
    }

    @Test
    public void createWithNotFoundUserTest() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> itemService.create(itemDto, 1));
    }

    @Test
    public void updateTest() {
        when(userService.get(1)).thenReturn(ownerDto);
        when(itemJpaRepository.save(any())).thenReturn(item);
        itemService.create(itemDto, owner.getId());
        when(itemJpaRepository.findById(1)).thenReturn(item);
        itemService.update(itemDto.getId(), owner.getId(), updateItemDto);
        assertEquals(updateItemDto.getId(), itemService.getItem(1).getId());
        assertEquals(updateItemDto.getName(), itemService.getItem(1).getName());
        assertEquals(updateItemDto.getDescription(), itemService.getItem(1).getDescription());
        assertEquals(updateItemDto.getAvailable(), itemService.getItem(1).getAvailable());
        assertEquals(updateItemDto.getRequestId(), itemService.getItem(1).getRequestId());
    }

    @Test
    public void updateNotFoundTest() {
        when(itemJpaRepository.findById(1)).thenReturn(item);
        assertThrows(IncorrectId.class, () -> itemService.update(1, 1, itemDto));
    }

    @Test
    public void updateUserNotOwnerTest() {
        when(itemJpaRepository.findById(anyInt())).thenReturn(item);
        assertThrows(IncorrectId.class, () -> itemService.update(item.getId(), owner.getId(), itemDto));
    }

    @Test
    public void getTest() {
        when(itemJpaRepository.findById(anyInt())).thenReturn(item);
        assertEquals(1, itemService.get(item.getId(), owner.getId()).getId());
    }

    @Test
    public void getNotFoundTest() {
        when(itemJpaRepository.findById(anyInt())).thenReturn(null);
        assertThrows(IncorrectId.class, () -> itemService.get(item.getId(), owner.getId()));
    }

    @Test
    public void getAllItemsByUserIdNotFoundTestFail() {
        when(userService.get(anyInt())).thenThrow(notFoundException);

        assertThrows(IncorrectId.class, () -> itemService.getAllByOwnerId(1, 0, 10));
    }

    @Test
    public void getByTextTest() {
        when(itemJpaRepository.findItemsContainingText(anyString(), any())).thenReturn(List.of(item));
        assertEquals(1, itemService.searchItemsByText("Назв", 0, 10).size());
    }
}
