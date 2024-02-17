package ru.practicum.shareit.booking.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ValidTimeRange
public class BookingCreateRequest {

    Long itemId;

    @FutureOrPresent(message = "Дата начала бронирования должна быть в настоящем или будущем")
    @NotNull(message = "Дата начала бронирования не должна быть пустой")
    LocalDateTime start;

    @Future(message = "Дата окончания бронирования должна быть в будущем")
    @NotNull(message = "Дата окончания бронирования не должна быть пустой")
    LocalDateTime end;
}
