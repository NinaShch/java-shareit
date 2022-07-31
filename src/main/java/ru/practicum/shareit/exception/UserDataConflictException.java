package ru.practicum.shareit.exception;

public class UserDataConflictException extends RuntimeException {
    public UserDataConflictException(String message) {
        super(message);
    }
}
