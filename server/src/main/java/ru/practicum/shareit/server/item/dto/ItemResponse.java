package ru.practicum.shareit.server.item.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemResponse {

    Long id;

    String name;

    String description;

    Boolean available;

    Long requestId;

    BookingView lastBooking;

    BookingView nextBooking;

    Set<CommentResponse> comments;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BookingView {
        Long id;

        Long bookerId;
    }
}
