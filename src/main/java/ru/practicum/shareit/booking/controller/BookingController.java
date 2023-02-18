package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingEntry;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer bookerId,
            @RequestBody BookingEntry bookingEntry) {
        return bookingService.create(bookingEntry, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto setBookingStatus(
            @PathVariable Integer bookingId,
            @RequestParam(required = false) Boolean approved,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return bookingService.reactToBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(
            @PathVariable Integer bookingId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsOfBooker(
            @RequestHeader("X-Sharer-User-Id") Integer bookerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsOfOwner(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return bookingService.getAllByOwnerId(ownerId, state, from, size);
    }
}
