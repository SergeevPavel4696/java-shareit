package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingEntry;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

public class BookingModelTest {
    private final LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    private final LocalDateTime end = LocalDateTime.of(2024, 2, 1, 0, 0, 0);
    private final BookingDto bookingDto1 = new BookingDto(1, start, end, null, null, WAITING);
    private final BookingDto bookingDto2 = BookingDto.builder().id(1).start(start).end(end).status(WAITING).build();
    private final BookingEntry bookingEntry1 = new BookingEntry(1, start, end, 1, 1, WAITING);
    private final BookingEntry bookingEntry2 = BookingEntry.builder().id(1).start(start).end(end).itemId(1).bookerId(1).status(WAITING).build();

    @Test
    public void bookingDtoTest() {
        bookingDto1.setStart(end);
        bookingDto2.setStart(end);
        assertEquals(bookingDto1.getStart(), bookingDto2.getStart());
        assertEquals(bookingDto1.getEnd(), bookingDto2.getEnd());
        assertEquals(bookingDto1.getStatus(), bookingDto2.getStatus());
    }

    @Test
    public void bookingEntryTest() {
        bookingEntry1.setStart(end);
        bookingEntry2.setStart(end);
        assertEquals(bookingEntry1.getStart(), bookingEntry2.getStart());
        assertEquals(bookingEntry1.getEnd(), bookingEntry2.getEnd());
        assertEquals(bookingEntry1.getStatus(), bookingEntry2.getStatus());
    }
}
