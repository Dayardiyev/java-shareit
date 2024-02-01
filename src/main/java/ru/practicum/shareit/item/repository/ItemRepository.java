package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    List<Item> findAllByUserId(long userId);

    Item create(long userId, Item item);

    Item update(long userId, long itemId, Item item);

    List<Item> findAllByName(long userId, String text);

    Optional<Item> findById(long itemId);

    Item findByUserIdAndItemId(long userId, long itemId);
}
