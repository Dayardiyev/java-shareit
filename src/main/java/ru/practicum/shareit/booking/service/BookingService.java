package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreateRequest;
import ru.practicum.shareit.booking.dto.BookingResponse;

import java.util.List;

public interface BookingService {
    List<BookingResponse> findAllByBookerId(long bookerId, String stateParam);

    List<BookingResponse> findAllByOwnerId(long ownerId, String stateParam);

    BookingResponse findByUserIdAndId(long bookingId, long userId);

    BookingResponse create(long userId, BookingCreateRequest booking);

    BookingResponse approve(long userId, long bookingId, boolean available);
}
