package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemCreateRequest;
import ru.practicum.shareit.item.dto.ItemResponse;
import ru.practicum.shareit.item.dto.ItemUpdateRequest;

import java.util.List;

public interface ItemService {
    List<ItemResponse> findAllByUserId(long userId);

    ItemResponse create(long userId, ItemCreateRequest itemCreateRequest);

    ItemResponse update(long userId, long itemId, ItemUpdateRequest itemUpdateRequest);

    List<ItemResponse> findAllByName(String text);

    ItemResponse findById(long itemId, long userId);
}
