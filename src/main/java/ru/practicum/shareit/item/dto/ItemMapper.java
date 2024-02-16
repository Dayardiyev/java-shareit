package ru.practicum.shareit.item.dto;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.dto.AbstractMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper extends AbstractMapper<Item, ItemResponse, ItemCreateRequest, ItemUpdateRequest> {

    @Mapping(target = "nextBooking",
            expression = "java(entity.getOwner().getId().equals(userId) ? mapToBookingView(entity.getNextBooking()) : null)")
    @Mapping(target = "lastBooking",
            expression = "java(entity.getOwner().getId().equals(userId) ? mapToBookingView(entity.getLastBooking()) : null)")
    ItemResponse mapToResponseEntity(Item entity, @Context long userId);

    @Mapping(source = "booker.id", target = "bookerId")
    ItemResponse.BookingView mapToBookingView(Booking booking);

    @Mapping(source = "author.name", target = "authorName")
    @Mapping(target = "created", source = "comment.createdAt")
    CommentResponse mapToResponseEntity(Comment comment);
}
