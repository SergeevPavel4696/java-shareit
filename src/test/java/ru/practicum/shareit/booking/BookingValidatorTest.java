package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.booking.validator.BookingIdValidator;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exceptions.IncorrectId;
import ru.practicum.shareit.exceptions.ServerError;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

public class BookingValidatorTest {
    BookingDto bookingDtoNull = null;
    LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0, 0);
    LocalDateTime end = LocalDateTime.of(2024, 2, 1, 0, 0, 0);
    User booker = new User(1, "booker", "booker@mail.ya");
    User owner = new User(2, "owner", "owner@mail.ya");
    Item item = new Item(1, "Название", "Описание", false, owner, null);
    Booking booking = Booking.builder().id(1).start(start).end(end).item(item).booker(booker).status(REJECTED).build();
    Booking bookingWithoutStart = Booking.builder().id(1).start(null).end(end).item(item).booker(booker).status(WAITING).build();
    Booking bookingWithoutEnd = Booking.builder().id(1).start(start).end(null).item(item).booker(booker).status(WAITING).build();
    Booking bookingWithoutBooker = Booking.builder().id(1).start(start).end(end).item(item).booker(null).status(WAITING).build();
    Booking bookingWithoutItem = Booking.builder().id(1).start(start).end(end).item(null).booker(booker).status(WAITING).build();
    Booking bookingWithoutStatus = Booking.builder().id(1).start(start).end(end).item(item).booker(booker).status(null).build();
    BookingEntry bookingEntryWithoutStart = new BookingEntry(1, null, end, 1, 2, WAITING);
    BookingEntry bookingEntryWithoutEnd = new BookingEntry(1, start, null, 1, 2, WAITING);
    BookingEntry bookingEntryWithoutItem = new BookingEntry(1, start, end, null, 2, WAITING);
    BookingEntry bookingEntryPastDate = new BookingEntry(1,
            LocalDateTime.of(2020,1,1,0,0,0),
            LocalDateTime.of(2020,2,1,0,0,0),
            1, 2, WAITING);
    BookingEntry bookingEntryWithIncorrectDate = new BookingEntry(1, end, start, 1, 2, WAITING);

    @Test
    public void bookingIdValidateTest() {
        assertThrows(IncorrectId.class, () -> BookingIdValidator.validate(bookingDtoNull));
    }

    @Test
    public void bookingValidateTest() {
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingWithoutStart));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingWithoutEnd));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingWithoutBooker));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingWithoutItem));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingWithoutStatus));
    }

    @Test
    public void bookingYourItemValidateTest() {
        assertThrows(IncorrectId.class, () -> BookingValidator.isYourItem(booking, owner.getId()));
    }

    @Test
    public void bookingValidateItemTest() {
        assertThrows(ValidationException.class, () -> BookingValidator.validateItem(item));
    }

    @Test
    public void bookingValidateApprovedTest() {
        assertThrows(ValidationException.class, () -> BookingValidator.validateApproved(false, booking));
    }

    @Test
    public void bookingValidateElsesItemTest() {
        assertThrows(IncorrectId.class, () -> BookingValidator.isElsesItem(booking, 1));
    }

    @Test
    public void bookingValidateElsesBookingTest() {
        assertThrows(IncorrectId.class, () -> BookingValidator.isElsesBooking(booking, 3));
    }

    @Test
    public void bookingEntryValidateTest() {
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingEntryWithoutStart));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingEntryWithoutEnd));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingEntryWithoutItem));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingEntryPastDate));
        assertThrows(ValidationException.class, () -> BookingValidator.validate(bookingEntryWithIncorrectDate));
    }

    @Test
    public void bookingValidateStatusTest() {
        assertThrows(ServerError.class, () -> BookingValidator.validateApproved(null));
    }
}
