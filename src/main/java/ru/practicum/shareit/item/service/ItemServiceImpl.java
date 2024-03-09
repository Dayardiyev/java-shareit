package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.common.exception.BadRequestException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;
    private final CommentRepository commentRepository;

    @Override
    public List<Item> findAllByUserId(long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.findAllByOwnerIdOrderById(userId, pageRequest)
                .toList();
    }

    @Override
    public Item create(long userId, Item item) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        item.setOwner(owner);

        return itemRepository.save(item);
    }

    @Override
    public Item update(long userId, long itemId, Item itemUpdate) {
        Item savedItem = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Вещь пользователя " + userId + " с id " + itemId + " не найден"));

        mapper.merge(savedItem, itemUpdate);
        return itemRepository.save(savedItem);
    }

    @Override
    public List<Item> findAllByName(String text, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        return itemRepository.findAllByNameContainingIgnoreCase(text, pageRequest)
                .toList();
    }

    @Override
    public Item findById(long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найден"));
    }

    @Override
    public Comment addComment(long userId, long itemId, Comment comment) {

        LocalDateTime time = LocalDateTime.now();

        Booking booking = bookingRepository
                .findByBookerIdAndItemIdAndStatusIsAndStartIsBefore(userId, itemId, Status.APPROVED, time, Pageable.unpaged())
                .stream()
                .findFirst()
                .orElseThrow(() -> new BadRequestException("Комментарий не создан, не найден бронь"));

        User user = booking.getBooker();
        Item item = booking.getItem();

        comment.setItem(item);
        comment.setAuthor(user);

        return commentRepository.save(comment);
    }
}

