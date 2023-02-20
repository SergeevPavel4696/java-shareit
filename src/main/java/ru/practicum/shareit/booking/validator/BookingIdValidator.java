package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.exceptions.IncorrectId;

public class BookingIdValidator {
    public static void validate(BookingDto bookingDto) {
        if (bookingDto == null) {
            throw new IncorrectId("Бронирование по указанному id не существует.");
        }
    }

    public static void validate(Booking booking) {
        if (booking == null) {
            throw new IncorrectId("Бронирование по указанному id не существует.");
        }
    }
}
