package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.repository.ItemRequestJpaRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemRequestServiceTest {
    @InjectMocks
    private ItemRequestService itemRequestService;
    @Mock
    private ItemRequestJpaRepository itemRequestJpaRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    private User user1;
    private UserDto user1Dto;
    private User user2;
    private UserDto user2Dto;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private final IncorrectId notFoundException = new IncorrectId("Объект не найден");

    @BeforeEach
    public void beforeEach() {
        user1 = new User(1, "Имя1", "1@mail.ya");
        user1Dto = UserMapper.convert(user1);
        user2 = new User(2, "Имя2", "2@mail.ya");
        user2Dto = UserMapper.convert(user2);
        item = new Item(1, "Название", "Описание", true, user1, 1);
        itemDto = ItemMapper.convert(item);
        LocalDateTime date = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        itemRequest = new ItemRequest(1, "Описание", user2, date);
        itemRequestDto = ItemRequestMapper.convert(itemRequest);
    }

    @Test
    public void createTest() {
        when(userService.get(1)).thenReturn(user1Dto);
        when(userService.getUser(1)).thenReturn(user1);
        when(itemRequestJpaRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDtoResponse itemRequestDtoResponse = itemRequestService.create(itemRequestDto, 1);
        assertEquals(itemRequest.getId(), itemRequestDtoResponse.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDtoResponse.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDtoResponse.getCreated());
    }

    @Test
    public void createUserNotFoundTest() {
        when(userService.getUser(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> itemRequestService.create(itemRequestDto, 10));
    }

    @Test
    public void getTest() {
        when(userService.get(anyInt())).thenReturn(user1Dto);
        when(itemRequestJpaRepository.findById(anyInt())).thenReturn(itemRequest);
        when(itemService.getAllByItemRequestId(anyInt())).thenReturn(List.of(itemDto));
        ItemRequestDtoWithItems itemRequestDto = itemRequestService.get(1, 1);
        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
        assertEquals(1, itemRequestDto.getItems().size());
    }

    @Test
    public void getNotFoundTest() {
        when(userService.get(anyInt())).thenReturn(user1Dto);
        when(itemRequestJpaRepository.findById(anyInt())).thenReturn(null);
        assertThrows(IncorrectId.class, () -> itemRequestService.get(1, 1));
    }

    @Test
    public void getUserNotFoundTest() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> itemRequestService.get(1, 10));
    }

    @Test
    public void getAllNotFoundTest() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> itemRequestService.getAllByRequesterId(10));
    }

    @Test
    public void getAllTest() {
        ItemRequest newItemRequest = new ItemRequest(2, "Описание", user2,
                LocalDateTime.of(2025, 1, 1, 0, 0, 0)
        );
        Item item = new Item(2, "Название", "Описание", true, user1, 2);
        when(userService.get(anyInt())).thenReturn(user2Dto);
        when(itemRequestJpaRepository.findAllByRequesterIdNotOrderByCreatedDesc(anyInt(), any())).thenReturn(List.of(newItemRequest));
        when(itemService.getAllByItemRequestId(anyInt())).thenReturn(List.of(ItemMapper.convert(item)));
        assertEquals(1, itemRequestService.getAllByOtherRequester(2, 0, 10).size());
    }

    @Test
    public void getAllRequestsUserNotFoundTestFail() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> itemRequestService.getAllByOtherRequester(2, 0, 10));
    }
}
