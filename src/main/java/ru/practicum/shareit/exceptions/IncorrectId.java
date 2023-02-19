package ru.practicum.shareit.exceptions;

public class IncorrectId extends RuntimeException {
    public IncorrectId(String message) {
        super(message);
    }
}
