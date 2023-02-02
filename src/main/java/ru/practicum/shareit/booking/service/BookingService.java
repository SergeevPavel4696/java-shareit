package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.booking.validator.BookingIdValidator;
import ru.practicum.shareit.booking.validator.BookingValidator;
import ru.practicum.shareit.exceptions.IncorrectBookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.validator.ItemIdValidator;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.UserIdValidator;

import java.time.LocalDateTime;
import java.util.Comparator;
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

    public Booking create(BookingDto bookingDto, int bookerId) {
        BookingValidator.validate(bookingDto, bookerId);
        ItemIdValidator.validate(itemService.getItemsId(), bookingDto.getItemId());
        UserIdValidator.validate(userService.getUsersId(), bookerId);
        Item item = itemService.getItem(bookingDto.getItemId());
        User booker = userService.getUser(bookerId);
        Booking booking = BookingMapper.convert(bookingDto, item, booker, WAITING);
        BookingValidator.validate(booking);
        BookingValidator.isYourItem(booking, bookerId);
        BookingValidator.validateItem(item);
        return bookingJpaRepository.save(booking);
    }

    public Booking reactToBooking(int bookingId, Boolean approved, int ownerId) {
        BookingIdValidator.validate(getBookingsId(), bookingId);
        BookingValidator.validateApproved(approved);
        UserIdValidator.validate(userService.getUsersId(), ownerId);
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
        return bookingJpaRepository.findById(bookingId);
    }

    public Booking get(int bookingId, int userId) {
        BookingIdValidator.validate(getBookingsId(), bookingId);
        UserIdValidator.validate(userService.getUsersId(), userId);
        Booking booking = bookingJpaRepository.findById(bookingId);
        BookingValidator.isElsesBooking(booking, userId);
        return booking;
    }

    public List<Booking> getAllOfBooker(int bookerId, String state) {
        UserIdValidator.validate(userService.getUsersId(), bookerId);
        List<Booking> bookings = bookingJpaRepository
                .findAll()
                .stream()
                .filter(booking -> booking.getBooker().getId() == bookerId)
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .collect(Collectors.toList());
        return getBookings(state, bookings);
    }

    public List<Booking> getAllOfOwner(int ownerId, String state) {
        UserIdValidator.validate(userService.getUsersId(), ownerId);
        List<Booking> bookings = bookingJpaRepository.findAll().stream()
                .filter(booking -> booking.getItem().getOwner().getId() == ownerId)
                .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
        return getBookings(state, bookings);
    }

    private List<Booking> getBookings(String state, List<Booking> bookings) {
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

    public List<Integer> getBookingsId() {
        return bookingJpaRepository.findAll().stream().map(Booking::getId).collect(Collectors.toList());
    }
}
