package ru.practicum.shareit.server.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.server.common.dto.AbstractMapper;
import ru.practicum.shareit.server.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper extends AbstractMapper<ItemRequest, ItemRequestResponse, ItemRequestCreateRequest, Void> {
}
