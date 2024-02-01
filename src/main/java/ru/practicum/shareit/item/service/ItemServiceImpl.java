package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<ItemDto> findAllByUserId(long userId) {
        return itemRepository.findAllByUserId(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        User user = checkIfUserExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);
        item = itemRepository.create(userId, item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        checkIfUserExists(userId);

        Item previousItem = itemRepository.findByUserIdAndItemId(userId, itemId);

        Item updatedItem = Item.builder()
                .id(itemId)
                .name(Objects.requireNonNullElse(itemDto.getName(), previousItem.getName()))
                .description(Objects.requireNonNullElse(itemDto.getDescription(), previousItem.getDescription()))
                .available(Objects.requireNonNullElse(itemDto.getAvailable(), previousItem.getAvailable()))
                .build();

        Item item = itemRepository.update(userId, itemId, updatedItem);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> findAllByName(long userId, String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        text = text.toLowerCase();
        return itemRepository.findAllByName(userId, text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto findById(long itemId) {
        Optional<Item> optionalItem = itemRepository.findById(itemId);
        if (optionalItem.isEmpty()) {
            throw new NotFoundException("Элемент с id=" + itemId + " не найден");
        }
        return ItemMapper.toItemDto(optionalItem.get());
    }

    private User checkIfUserExists(long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", userId)));
    }
}

