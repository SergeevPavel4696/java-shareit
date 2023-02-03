package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ServerError;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

public class BookingValidator {
    public static void validate(Booking booking) {
        if (booking.getStart() == null) {
            throw new ValidationException("Время начала аренды вещи обязательное.");
        }
        if (booking.getEnd() == null) {
            throw new ValidationException("Время конца аренды вещи обязательное.");
        }
        if (booking.getBooker() == null) {
            throw new ValidationException("Арендатор обязателен.");
        }
        if (booking.getItem() == null) {
            throw new ValidationException("Арендуемая вещь обязательна.");
        }
        if (booking.getStatus() == null) {
            throw new ValidationException("Статус аренды вещи обязателен.");
        }
    }

    public static void isYourItem(Booking booking, int ownerId) {
        if (booking.getItem().getOwner().getId() == ownerId) {
            throw new IncorrectId("Нельзя забронировать свою вещь.");
        }
    }

    public static void validateItem(Item item) {
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна к бронированию.");
        }
    }

    public static void validateApproved(Boolean approved) {
        if (approved == null) {
            throw new ServerError("Статус утверждения обязателен.");
        }
    }

    public static void validateApproved(Boolean approved, Booking booking) {
        if (approved && booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Бронирование уже подтверждено.");
        }
        if (!approved && booking.getStatus() == BookingStatus.REJECTED) {
            throw new ValidationException("Бронирование уже отклонено.");
        }
    }

    public static void isElsesItem(Booking booking, int ownerId) {
        if (booking.getItem().getOwner().getId() != ownerId) {
            throw new IncorrectId("Вы не можете реагировать на бронирование чужих вещей.");
        }
    }

    public static void isElsesBooking(Booking booking, int userId) {
        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new IncorrectId("Вы не можете смотреть чужие бронирования.");
        }
    }

    public static void validate(BookingEntry bookingEntry, Integer bookerId) {
        if (bookerId == null) {
            throw new ServerError("Неопределён арендатор вещи.");
        }
        if (bookingEntry.getStart() == null) {
            throw new ValidationException("Время начала аренды вещи обязательное.");
        }
        if (bookingEntry.getEnd() == null) {
            throw new ValidationException("Время конца аренды вещи обязательное.");
        }
        if (bookingEntry.getItemId() == null) {
            throw new ValidationException("Арендуемая вещь обязательна.");
        }
        if (bookingEntry.getEnd().isBefore(LocalDateTime.now()) || bookingEntry.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Время бронирования уже прошло.");
        }
        if (bookingEntry.getStart().isAfter(bookingEntry.getEnd())) {
            throw new ValidationException("Некорректные время начала и конца бронирования.");
        }
    }
}
