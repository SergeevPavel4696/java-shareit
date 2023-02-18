package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    private final int bookerId = 1;
    private final String userName = "Имя";
    private final String bookerEmail = "mail@mail.ya";
    private final User booker = new User(bookerId, userName, bookerEmail);

    private final int ownerId = 2;
    private final String ownerName = "Имя";
    private final String ownerEmail = "mail@mail.ya";
    private final User owner = new User(ownerId, ownerName, ownerEmail);

    private final int itemId = 3;
    private final String itemName = "Название";
    private final String description = "Описание";
    private final boolean available = true;
    private final int itemRequestId = 4;
    private final Item item = new Item(itemId, itemName, description, available, owner, itemRequestId);

    private final int bookingId = 5;
    private final LocalDateTime start = LocalDateTime.of(2020, 10, 15, 12, 40, 35);
    private final LocalDateTime end = LocalDateTime.of(2020, 10, 20, 12, 40, 35);
    private final BookingStatus status = BookingStatus.WAITING;
    private final Booking booking = new Booking(bookingId, start, end, item, booker, status);
    private final BookingEntry bookingEntry = new BookingEntry(bookingId, start, end, itemId, bookerId, status);

    @Test
    public void BookingToBookingDtoTest() {
        BookingDto bookingDto = BookingMapper.convertToDto(booking);
        assertEquals(bookingId, bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item, bookingDto.getItem());
        assertEquals(booker, bookingDto.getBooker());
        assertEquals(status, bookingDto.getStatus());
    }

    @Test
    public void BookingEntryToBookingTest() {
        Booking booking = BookingMapper.convert(bookingEntry, item, booker, status);
        assertEquals(bookingId, booking.getId());
        assertEquals(start, booking.getStart());
        assertEquals(end, booking.getEnd());
        assertEquals(item, booking.getItem());
        assertEquals(booker, booking.getBooker());
        assertEquals(status, booking.getStatus());
    }

    @Test
    public void BookingToBookingEntryTest() {
        BookingEntry bookingEntry = BookingMapper.convert(booking);
        assertEquals(bookingId, bookingEntry.getId());
        assertEquals(start, bookingEntry.getStart());
        assertEquals(end, bookingEntry.getEnd());
        assertEquals(itemId, bookingEntry.getItemId());
        assertEquals(bookerId, bookingEntry.getBookerId());
        assertEquals(status, bookingEntry.getStatus());
    }
}
