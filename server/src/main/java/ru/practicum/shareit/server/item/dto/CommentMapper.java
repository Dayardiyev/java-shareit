package ru.practicum.shareit.server.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.server.common.dto.AbstractMapper;
import ru.practicum.shareit.server.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper extends AbstractMapper<Comment, CommentResponse, CommentCreateRequest, Void> {
    @Override
    @Mapping(target = "authorName",
            expression = "java(entity.getAuthor().getName())")
    @Mapping(target = "created", source = "entity.createdAt")
    CommentResponse mapToResponseEntity(Comment entity);
}
