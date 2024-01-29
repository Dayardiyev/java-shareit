package ru.practicum.shareit.exception;

public class FieldNotFilledException extends RuntimeException {
    public FieldNotFilledException(String message) {
        super(message);
    }
}
