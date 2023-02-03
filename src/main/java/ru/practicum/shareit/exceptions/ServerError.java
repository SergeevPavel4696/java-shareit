package ru.practicum.shareit.exceptions;

public class ServerError extends RuntimeException {
    public ServerError(String message) {
        super(message);
    }
}
