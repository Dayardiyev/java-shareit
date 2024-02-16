package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, APPROVED, REJECTED;

    public static BookingState parse(String value) {
        for (BookingState state : values()) {
            if (state.name().equals(value)) {
                return state;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + value);
    }
}
