package ru.practicum.shareit.booking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public Booking createBooking(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer bookerId,
            @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookingDto, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public Booking setBookingStatus(
            @PathVariable Integer bookingId,
            @RequestParam(required = false) Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return bookingService.reactToBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public Booking getBookingById(
            @PathVariable Integer bookingId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<Booking> getAllBookingsOfBooker(
            @RequestHeader("X-Sharer-User-Id") Integer bookerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllOfBooker(bookerId, state);
    }

    @GetMapping("/owner")
    public List<Booking> getAllBookingsOfOwner(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        return bookingService.getAllOfOwner(ownerId, state);
    }
}
