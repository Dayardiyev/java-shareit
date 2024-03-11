package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.common.dto.AbstractMapper;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper extends AbstractMapper<ItemRequest, ItemRequestResponse, ItemRequestCreateRequest, Void> {
}
