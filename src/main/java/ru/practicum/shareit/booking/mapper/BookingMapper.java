package ru.practicum.shareit.booking.mapper;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BookingMapper {
    public static BookingEntry convert(Booking booking) {
        return new BookingEntry(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem().getId(), booking.getBooker().getId(), booking.getStatus());
    }

    public static Booking convert(BookingEntry bookingEntry, Item item, User booker, BookingStatus status) {
        return new Booking(bookingEntry.getId(), bookingEntry.getStart(), bookingEntry.getEnd(), item, booker, status);
    }

    public static BookingDto convertToDto(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem(), booking.getBooker(), booking.getStatus());
    }
}
