package ru.practicum.shareit.server.item.service;

import ru.practicum.shareit.server.item.model.Comment;
import ru.practicum.shareit.server.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> findAllByUserId(long userId, int from, int size);

    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    List<Item> findAllByName(String text, int from, int size);

    Item findById(long itemId);

    Comment addComment(long userId, long itemId, Comment comment);
}
