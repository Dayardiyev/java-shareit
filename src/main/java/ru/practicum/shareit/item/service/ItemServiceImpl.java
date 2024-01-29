package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.FieldNotFilledException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;

    @Override
    public List<Item> findAllByUserId(long userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public Item create(long userId, Item item) {
        validate(item);
        return repository.create(userId, item);
    }

    @Override
    public Item update(long userId, long itemId, Item item) {
        return repository.update(userId, itemId, item);
    }

    @Override
    public List<Item> findAllByName(long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        text = text.toLowerCase();
        return repository.findAllByName(userId, text);
    }

    @Override
    public Item findByItemId(long itemId) {
        return repository.findByItemId(itemId);
    }

    private void validate(Item item) {
        if (item.getAvailable() == null
                || item.getName() == null || item.getName().isEmpty()
                || item.getDescription() == null || item.getDescription().isEmpty()
        ) {
            throw new FieldNotFilledException("Поле не заполнен");
        }
    }
}
