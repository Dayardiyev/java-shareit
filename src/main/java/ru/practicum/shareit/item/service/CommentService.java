package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentCreateRequest;
import ru.practicum.shareit.item.dto.CommentResponse;

public interface CommentService {
    CommentResponse create(long userId, long itemId, CommentCreateRequest commentCreateRequest);
}
