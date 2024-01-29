package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> findAllByUserId(long userId);

    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    List<Item> findAllByName(long userId, String text);

    Item findByItemId(long itemId);
}
