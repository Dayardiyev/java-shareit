package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public List<ItemResponse> findAllByUserId(long userId) {
        return mapper.mapToResponseEntity(itemRepository.findAllByOwnerIdOrderById(userId));
    }

    @Override
    public ItemResponse create(long userId, ItemCreateRequest itemCreateRequest) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Item item = mapper.mapFromCreateRequestDto(itemCreateRequest);
        item.setOwner(owner);
        return mapper.mapToResponseEntity(itemRepository.save(item));
    }

    @Override
    public ItemResponse update(long userId, long itemId, ItemUpdateRequest itemUpdate) {
        Item savedItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Вещь пользователя " + userId + " с id " + itemId + " не найден"));

        mapper.merge(savedItem, mapper.mapFromUpdateRequestDto(itemUpdate));
        savedItem = itemRepository.save(savedItem);

        return mapper.mapToResponseEntity(savedItem);
    }

    @Override
    public List<ItemResponse> findAllByName(String text) {
        List<Item> items = itemRepository.findAllByNameContainingIgnoreCase(text);

        return mapper.mapToResponseEntity(items);
    }

    @Override
    public ItemResponse findById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден"));

        return mapper.mapToResponseEntity(item, userId);
    }
}

