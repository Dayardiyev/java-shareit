package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items;

    private static long serial = 1;

    public ItemRepositoryImpl() {
        items = new HashMap<>();
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.get(userId);
    }

    @Override
    public Item create(long userId, Item item) {
        item.setId(getUniqueId());
        items.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        items.computeIfPresent(userId, (key, userItems) -> {
            int index = IntStream.range(0, userItems.size())
                    .filter(i -> userItems.get(i).getId() == itemId)
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(String.format("Элемент с id=%d не найден для пользователя с id=%d", itemId, userId)));

            userItems.set(index, item);
            return userItems;
        });
        return item;
    }

    @Override
    public Optional<Item> findById(long itemId) {
        return items.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst();
    }

    @Override
    public List<Item> findAllByName(long userId, String text) {
        return items.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getAvailable() &&
                        (containsIgnoreCase(item.getName(), text) ||
                                containsIgnoreCase(item.getDescription(), text)))
                .collect(Collectors.toList());
    }

    @Override
    public Item findByUserIdAndItemId(long userId, long itemId) {
        return items.getOrDefault(userId, new ArrayList<>())
                .stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Элемент с id=%d не найден для пользователя с id=%d", itemId, userId)));
    }

    private boolean containsIgnoreCase(String source, String target) {
        return source != null && source.toLowerCase().contains(target.toLowerCase());
    }

    private long getUniqueId() {
        return serial++;
    }
}
