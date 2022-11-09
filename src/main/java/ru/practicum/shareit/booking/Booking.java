package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

/**
* TODO Sprint add-bookings.
*/
@Data
@Builder
@AllArgsConstructor
public class Booking {
    private int id;
    private LocalDateTime startBooking;
    private LocalDateTime endBooking;
    private Item item;
    private User booker;
    private BookingStatus status;
}
