package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;

public interface CommentService {
    Comment addComment(long userId, long itemId, Comment comment);
}
