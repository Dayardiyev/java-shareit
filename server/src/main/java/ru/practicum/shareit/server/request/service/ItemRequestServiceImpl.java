package ru.practicum.shareit.server.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.server.common.exception.NotFoundException;
import ru.practicum.shareit.server.request.model.ItemRequest;
import ru.practicum.shareit.server.request.repository.ItemRequestRepository;
import ru.practicum.shareit.server.user.model.User;
import ru.practicum.shareit.server.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequest create(long userId, ItemRequest itemRequest) {
        User user = getUserById(userId);
        itemRequest.setAuthor(user);
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequest> findAllByAuthor(long authorId) {
        User author = getUserById(authorId);
        return itemRequestRepository.findAllByAuthor(author);
    }

    @Override
    public List<ItemRequest> findAll(long userId, int from, int size) {
        User user = getUserById(userId);

        return itemRequestRepository
                .findAllByAuthorNotOrderByCreated(user, PageRequest.of(from / size, size))
                .toList();
    }

    @Override
    public ItemRequest findById(long userId, long requestId) {
        getUserById(userId);

        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id=" + requestId + " не найден!"));
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден!"));
    }
}
