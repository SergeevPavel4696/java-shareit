package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.exceptions.IncorrectId;

import java.util.List;

public class BookingIdValidator {
    public static void validate(List<Integer> bookingsId, Integer bookingId) {
        if (!bookingsId.contains(bookingId)) {
            throw new IncorrectId("Бронирование по указанному id не существует.");
        }
    }
}
