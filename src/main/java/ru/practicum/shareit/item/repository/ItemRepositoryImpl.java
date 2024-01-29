package ru.practicum.shareit.item.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items;

    private final UserRepository userRepository;

    private static long serial = 1;

    @Autowired
    public ItemRepositoryImpl(UserRepository userRepository) {
        items = new HashMap<>();
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> findAllByUserId(long userId) {
        return items.get(userId);
    }

    @Override
    public Item create(long userId, Item item) {
        checkIfUserExists(userId);
        item.setId(getUniqueId());
        items.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);
        return item;
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        checkIfUserExists(userId);

        Item previousItem = findByUserIdAndItemId(userId, itemId);

        Item updatedItem = Item.builder()
                .id(itemId)
                .name(Objects.requireNonNullElse(item.getName(), previousItem.getName()))
                .description(Objects.requireNonNullElse(item.getDescription(), previousItem.getDescription()))
                .available(Objects.requireNonNullElse(item.getAvailable(), previousItem.getAvailable()))
                .build();


        items.computeIfPresent(userId, (key, userItems) -> {
            int index = IntStream.range(0, userItems.size())
                    .filter(i -> userItems.get(i).getId() == itemId)
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException(String.format("Элемент с id=%d не найден для пользователя с id=%d", itemId, userId)));

            userItems.set(index, updatedItem);
            return userItems;
        });
        return updatedItem;
    }

    @Override
    public Item findByItemId(long itemId) {
        return items.values().stream()
                .flatMap(List::stream)
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElse(null);
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

    private boolean containsIgnoreCase(String source, String target) {
        return source != null && source.toLowerCase().contains(target.toLowerCase());
    }


    private void checkIfUserExists(long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователь с id=%d не найден", userId));
        }
    }


    private Item findByUserIdAndItemId(long userId, long itemId) {
        return items.getOrDefault(userId, new ArrayList<>())
                .stream()
                .filter(item -> item.getId() == itemId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(String.format("Элемент с id=%d не найден для пользователя с id=%d", itemId, userId)));
    }

    private long getUniqueId() {
        return serial++;
    }
}
