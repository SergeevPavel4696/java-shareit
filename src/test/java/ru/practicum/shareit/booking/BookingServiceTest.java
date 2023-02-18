package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.IncorrectBookingStatus;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BookingServiceTest {
    @InjectMocks
    private BookingService bookingService;
    @Mock
    private BookingJpaRepository bookingJpaRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    private User booker;
    private UserDto bookerDto;
    private User user;
    private UserDto userDto;
    private User owner;
    private UserDto ownerDto;
    private Item item;
    private Booking booking;
    private final IncorrectId notFoundException = new IncorrectId("Объект не найден");

    @BeforeEach
    public void beforeEach() {
        booker = new User(1, "Имя1", "first@email.com");
        bookerDto = UserMapper.convert(booker);
        user = new User(2, "Имя2", "second@email.com");
        userDto = UserMapper.convert(user);
        owner = new User(3, "Владелец", "owner@mail.ya");
        ownerDto = UserMapper.convert(owner);
        item = new Item(1, "Название", "Описание", true, owner, 1);
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 0, 0, 0);
        booking = new Booking(1, start, end, item, booker, APPROVED);
    }

    @Test
    public void createTest() {
        when(userService.get(anyInt())).thenReturn(ownerDto);
        when(itemService.getItem(anyInt())).thenReturn(item);
        when(bookingJpaRepository.save(any())).thenReturn(booking);
        BookingDto bookingDto = bookingService.create(BookingMapper.convert(booking), 1);
        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItem(), bookingDto.getItem());
        assertEquals(booking.getBooker(), bookingDto.getBooker());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    public void createUnavailableItemTest() {
        item.setAvailable(false);
        when(itemService.getItem(anyInt())).thenReturn(item);
        assertThrows(IncorrectId.class, () -> bookingService.create(BookingMapper.convert(booking), 1));
    }

    @Test
    public void createSelfBookingTest() {
        when(itemService.getItem(anyInt())).thenReturn(item);
        assertThrows(IncorrectId.class, () -> bookingService.create(BookingMapper.convert(booking), 2));
    }

    @Test
    public void createEndIsBeforeStartTest() {
        booking.setEnd(booking.getStart().minusDays(1));
        when(itemService.getItem(anyInt())).thenReturn(item);
        assertThrows(ValidationException.class, () -> bookingService.create(BookingMapper.convert(booking), 1));
    }

    @Test
    public void createNotFoundTest() {
        when(itemService.getItem(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> bookingService.create(BookingMapper.convert(booking), 1));
    }

    @Test
    public void createUserNotFoundTest() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        when(itemService.getItem(anyInt())).thenReturn(item);
        assertThrows(IncorrectId.class, () -> bookingService.create(BookingMapper.convert(booking), 10));
    }

    @Test
    public void updateTest() {
        when(userService.get(anyInt())).thenReturn(userDto);
        when(bookingJpaRepository.findById(1)).thenReturn(booking);
        BookingDto updatedBooking = bookingService.reactToBooking(1, false, owner.getId());
        assertEquals(updatedBooking.getStatus(), APPROVED);
    }

    @Test
    public void updateNotFoundTest() {
        when(bookingJpaRepository.findById(1)).thenReturn(booking);
        assertThrows(IncorrectId.class, () -> bookingService.reactToBooking(1, false, owner.getId()));
    }

    @Test
    public void updateUserNotFoundTest() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> bookingService.reactToBooking(1, false, 10));
    }

    @Test
    public void updateAlreadyApprovedTest() {
        when(userService.get(anyInt())).thenReturn(userDto);
        when(bookingJpaRepository.findById(anyInt())).thenReturn(booking);
        assertThrows(ValidationException.class, () -> bookingService.reactToBooking(1, true, 2));
    }

    @Test
    public void getTest() {
        when(userService.get(anyInt())).thenReturn(userDto);
        when(bookingJpaRepository.findById(anyInt())).thenReturn(booking);
        when(itemService.getItem(anyInt())).thenReturn(item);
        assertEquals(1, bookingService.getBooking(1).getId());
    }

    @Test
    public void getNotFoundTest() {
        when(userService.get(anyInt())).thenReturn(userDto);
        when(bookingJpaRepository.findById(anyInt())).thenReturn(null);
        assertThrows(IncorrectId.class, () -> bookingService.getBooking(1));
    }

    @Test
    public void getUserNotFoundTest() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> bookingService.get(1, 10));
    }

    @Test
    public void getAllByUserIdNotFoundTestFail() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> bookingService.getAllByBookerId(1, "ALL", 0, 10));
    }

    @Test
    public void getAllByUserIdUnsupportedStateTestFail() {
        when(userService.get(anyInt())).thenReturn(bookerDto);
        assertThrows(IncorrectBookingStatus.class, () -> bookingService.getAllByBookerId(1, "UNSUPPORTED", 0, 10));
    }

    @Test
    public void getAllByOwnerIdNotFoundTestFail() {
        when(userService.get(anyInt())).thenThrow(notFoundException);
        assertThrows(IncorrectId.class, () -> bookingService.getAllByOwnerId(2, "ALL", 0, 10));
    }

    @Test
    public void getAllBookingsByOwnerIdUnsupportedStateTestFail() {
        when(userService.get(anyInt())).thenReturn(userDto);
        assertThrows(IncorrectBookingStatus.class, () -> bookingService.getAllByOwnerId(2, "UNSUPPORTED", 0, 10));
    }
}
