package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    List<Booking> findAllByBookerId(long bookerId, BookingState stateParam, int from, int size);

    List<Booking> findAllByOwnerId(long ownerId, BookingState stateParam, int from, int size);

    Booking findByUserIdAndId(long bookingId, long userId);

    Booking create(long userId, Booking booking);

    Booking approve(long userId, long bookingId, boolean available);
}
