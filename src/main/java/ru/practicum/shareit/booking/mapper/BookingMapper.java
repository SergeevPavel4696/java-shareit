package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto convert(Booking booking) {
        return new BookingDto(booking.getId(), booking.getStart(), booking.getEnd(),
                booking.getItem().getId(), booking.getBooker().getId(), booking.getStatus());
    }

    public static Booking convert(BookingDto bookingDto, Item item, User booker, BookingStatus status) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), item, booker, status);
    }

    public static Booking convert(BookingDto bookingDto, Item item, User booker) {
        return new Booking(bookingDto.getId(), bookingDto.getStart(), bookingDto.getEnd(), item, booker, bookingDto.getStatus());
    }
}
