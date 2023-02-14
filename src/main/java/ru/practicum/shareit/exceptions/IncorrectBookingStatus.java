package ru.practicum.shareit.exceptions;

public class IncorrectBookingStatus extends RuntimeException {
    public IncorrectBookingStatus(String message) {
        super(message);
    }
}
