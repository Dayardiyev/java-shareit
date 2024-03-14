package ru.practicum.shareit.server.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.server.booking.model.Booking;
import ru.practicum.shareit.server.common.dto.AbstractMapper;

@Mapper(componentModel = "spring")
public interface BookingMapper extends AbstractMapper<Booking, BookingResponse, BookingCreateRequest, Void> {
    @Override
    @Mapping(target = "item.id", source = "itemId")
    Booking mapFromCreateRequestDto(BookingCreateRequest entityCreateRequestDto);
}
