package ru.practicum.shareit.server.booking.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.server.booking.model.Status;
import ru.practicum.shareit.server.item.dto.ItemResponse;
import ru.practicum.shareit.server.user.dto.UserResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingResponse {

    Long id;

    LocalDateTime start;

    LocalDateTime end;

    Status status;

    UserResponse booker;

    ItemResponse item;
}
