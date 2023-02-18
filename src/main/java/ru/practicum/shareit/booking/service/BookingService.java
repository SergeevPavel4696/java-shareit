package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.validator.BookingIdValidator;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exceptions.Duplicate;
import ru.practicum.shareit.exceptions.IncorrectBookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validator.ItemIdValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.UserIdValidator;
import ru.practicum.shareit.util.PageableMaker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.model.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingJpaRepository bookingJpaRepository;

    public BookingDto create(BookingEntry bookingEntry, int bookerId) {
        BookingValidator.validate(bookingEntry, bookerId);
        ItemIdValidator.validate(itemService.getItem(bookingEntry.getItemId()));
        UserIdValidator.validate(userService.get(bookerId));
        Item item = itemService.getItem(bookingEntry.getItemId());
        User booker = userService.getUser(bookerId);
        Booking booking = BookingMapper.convert(bookingEntry, item, booker, WAITING);
        BookingValidator.isYourItem(booking, bookerId);
        BookingValidator.validateItem(item);
        try {
            return BookingMapper.convertToDto(bookingJpaRepository.save(booking));
        } catch (DataIntegrityViolationException e) {
            throw new Duplicate("Вещь с указанными данными уже существует.");
        }
    }

    public BookingDto reactToBooking(int bookingId, Boolean approved, int ownerId) {
        BookingIdValidator.validate(getBooking(bookingId));
        BookingValidator.validateApproved(approved);
        UserIdValidator.validate(userService.get(ownerId));
        Booking booking = bookingJpaRepository.findById(bookingId);
        BookingValidator.validateApproved(approved, booking);
        BookingValidator.isElsesItem(booking, ownerId);
        if (approved) {
            BookingValidator.validateItem(booking.getItem());
            bookingJpaRepository.save(new Booking(booking.getId(), booking.getStart(),
                    booking.getEnd(), booking.getItem(), booking.getBooker(), APPROVED));
        } else {
            bookingJpaRepository.save(new Booking(booking.getId(), booking.getStart(),
                    booking.getEnd(), booking.getItem(), booking.getBooker(), REJECTED));
        }
        return BookingMapper.convertToDto(bookingJpaRepository.findById(bookingId));
    }

    public BookingDto get(int bookingId, int userId) {
        UserIdValidator.validate(userService.get(userId));
        Booking booking = bookingJpaRepository.findById(bookingId);
        BookingIdValidator.validate(booking);
        BookingValidator.isElsesBooking(booking, userId);
        return BookingMapper.convertToDto(booking);
    }

    public Booking getBooking(int bookingId) {
        Booking booking = bookingJpaRepository.findById(bookingId);
        BookingIdValidator.validate(booking);
        return booking;
    }

    public List<BookingDto> getAllByBookerId(int bookerId, String state, Integer from, Integer size) {
        UserIdValidator.validate(userService.get(bookerId));
        List<BookingDto> bookings = bookingJpaRepository
                .findAllByBooker_IdOrderByStartDesc(bookerId, PageableMaker.makePage(from, size))
                .stream()
                .map(BookingMapper::convertToDto)
                .collect(Collectors.toList());
        return getBookings(state, bookings);
    }

    public List<BookingDto> getAllByOwnerId(int ownerId, String state, Integer from, Integer size) {
        UserIdValidator.validate(userService.get(ownerId));
        List<BookingDto> bookings = bookingJpaRepository
                .findAllByItem_Owner_IdOrderByStartDesc(ownerId, PageableMaker.makePage(from, size))
                .stream()
                .map(BookingMapper::convertToDto)
                .collect(Collectors.toList());
        return getBookings(state, bookings);
    }

    private List<BookingDto> getBookings(String state, List<BookingDto> bookings) {
        switch (state) {
            case "ALL":
                return bookings;
            case "FUTURE":
                return bookings.stream()
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "CURRENT":
                return bookings.stream()
                        .filter(booking -> (booking.getEnd().isAfter(LocalDateTime.now()))
                                && booking.getStart().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(WAITING))
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(booking -> booking.getStatus().equals(REJECTED))
                        .collect(Collectors.toList());
            default:
                throw new IncorrectBookingStatus("Некорректный статус бронирования");
        }
    }
}
