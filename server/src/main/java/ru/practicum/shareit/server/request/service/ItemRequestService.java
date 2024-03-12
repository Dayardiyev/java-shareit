package ru.practicum.shareit.server.request.service;

import ru.practicum.shareit.server.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequest create(long userId, ItemRequest itemRequest);

    List<ItemRequest> findAllByAuthor(long authorId);

    List<ItemRequest> findAll(long userId, int from, int size);

    ItemRequest findById(long userId, long requestId);
}
